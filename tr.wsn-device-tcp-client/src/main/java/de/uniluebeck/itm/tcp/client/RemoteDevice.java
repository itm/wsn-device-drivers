package de.uniluebeck.itm.tcp.client;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientBootstrap;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.MessagePlainText;
import de.uniluebeck.itm.devicedriver.MessagePlainTextListener;
import de.uniluebeck.itm.devicedriver.PacketType;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;
import de.uniluebeck.itm.tcp.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.FlashData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.MacData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.PacketService;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.PacketServiceAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.PacketTypeData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.ProgramPacket;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.STRING;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Timeout;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.BlockingInterface;


public class RemoteDevice implements DeviceAsync{

	private static Logger log = LoggerFactory.getLogger(RemoteDevice.class);
	
	ThreadPoolCallExecutor executor = null;
	DuplexTcpClientBootstrap bootstrap = null;
	RpcClientChannel channel = null;
	Operations.Interface operationService = null;
	PacketService.Interface packetService = null;
	PacketServiceAnswerImpl packetServiceAnswerImpl = null;
	RemoteConnection connection = null;

	RemoteDevice(RemoteConnection connection){
		this.connection = connection;
		
		this.channel = connection.getChannel();
		this.bootstrap = connection.getBootstrap();
		operationService = Operations.newStub(channel);
		packetService = PacketService.newStub(channel);
		packetServiceAnswerImpl = new PacketServiceAnswerImpl();
		
		// registrieren der Reverse-RPC Services
		bootstrap.getRpcServiceRegistry().registerService(PacketServiceAnswer.newReflectiveService(packetServiceAnswerImpl));

	}

	@Override
	public OperationHandle<Void> program(byte[] bytes,
			long timeout, final AsyncCallback<Void> callback) {
		
		final RpcController controller = channel.newRpcController();
		
		ProgramPacket packet = ProgramPacket.newBuilder().addBinaryPacket(ByteString.copyFrom(bytes)).setTimeout(timeout).setOperationKey(controller.toString()).build();
		
		operationService.program(controller, packet, new RpcCallback<EmptyAnswer>() {
			@Override
			public void run(EmptyAnswer parameter) {
				if(controller.failed()){
					callback.onFailure(new Throwable(controller.errorText()));
				}
				else{
					callback.onSuccess(null);
				}
			}
		});
		
		// Rueckgabe eines OperationHandle
		return new OperationHandle<Void>(){

			// aufruf bei cancel (User-seitig)
			@Override
			public void cancel() {
				// theoretisch soll das so gehen,
				// ist aber noch nicht getestet
				controller.startCancel();
				
				//alternativer async RPC aufruf zum abbrechen
//				operationService.cancelHandle(controller, VOID.newBuilder().build(), new RpcCallback<VOID>(){
//
//					@Override
//					public void run(VOID parameter) {
//						callback.onCancel();
//					}});
			}

			// aufruf bei get (User-seitig)
			@Override
			public Void get() {

				// erzeugen eines sync RPC-Objekts fuer die Operationen
		        BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
		        try {
		        	// sync RPC-Aufruf
					blockOperationService.getHandle(controller, OpKey.newBuilder().setOperationKey(controller.toString()).build());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			// aufruf bei getState (User-seitig)
			@Override
			public State getState() {
				
				// erzeugen eines State-Objekts
				State state = null;

				// Blockierender Aufruf fuer getState in Ordnung?
				
				// erzeugen eines sync RPC-Objekts fuer die Operationen
				BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
				try {
					// sync RPC-Aufruf
					STRING result = blockOperationService.getState(controller,  OpKey.newBuilder().setOperationKey(controller.toString()).build());
					// erzeugen eines State aus dem result-String
					state = State.fromName(result.getQuery());
				} catch (ServiceException e) {
					e.printStackTrace();
				}

				//alternativer async RPC aufruf
//				operationService.getState(controller, VOID.newBuilder().setHandleKey(controller.toString()).build(), new RpcCallback<STRING>(){
//					@Override
//					public void run(STRING parameter) {
//						System.out.println(parameter.getQuery());
//						state = State.fromName(parameter.getQuery());
//					}});
				
				return state;
			}};
	}

	@Override
	public OperationHandle<Void> eraseFlash(long timeout,
			AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<byte[]> readFlash(int address, int length,
			long timeout, AsyncCallback<byte[]> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<MacAddress> readMac(long timeout,
			final AsyncCallback<MacAddress> callback) {
		
		final RpcController controller = channel.newRpcController();
		
		Timeout request = Timeout.newBuilder().setOperationKey(controller.toString()).setTimeout(timeout).build();

		packetServiceAnswerImpl.addCallback(request.getOperationKey(), callback);
		
		operationService.readMac(controller, request, new RpcCallback<EmptyAnswer>() {
			@Override
			public void run(EmptyAnswer parameter) {
				if(controller.failed()){
					callback.onFailure(new Throwable(controller.errorText()));
				}
			}
		});
		
		// Rueckgabe eines OperationHandle
		return new OperationHandle<MacAddress>(){

			// aufruf bei cancel (User-seitig)
			@Override
			public void cancel() {
				controller.startCancel();
			}

			// aufruf bei get (User-seitig)
			@Override
			public MacAddress get() {

				//TODO get genauer anschauen
				
				// erzeugen eines sync RPC-Objekts fuer die Operationen
		        BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
		        try {
		        	// sync RPC-Aufruf
		        	blockOperationService.getHandle(controller, OpKey.newBuilder().setOperationKey(controller.toString()).build());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return null;//TODO MacAdresse zurueckgeben
			}
			
			// aufruf bei getState (User-seitig)
			@Override
			public State getState() {
				
				// erzeugen eines State-Objekts
				State state = null;

				// Blockierender Aufruf fuer getState in Ordnung?
				
				// erzeugen eines sync RPC-Objekts fuer die Operationen
				BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
				try {
					// sync RPC-Aufruf
					STRING result = blockOperationService.getState(controller,  OpKey.newBuilder().setOperationKey(controller.toString()).build());
					// erzeugen eines State aus dem result-String
					state = State.fromName(result.getQuery());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				
				return state;
			}};
	}

	@Override
	public OperationHandle<Void> reset(long timeout,
			AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<Void> send(MessagePacket packet, long timeout,
			AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<Void> writeFlash(int address, byte[] data,
			int length, long timeout, final AsyncCallback<Void> callback) {
		
		final RpcController controller = channel.newRpcController();
		
		FlashData flash = FlashData.newBuilder().setAddress(address).addData(ByteString.copyFrom(data)).setLength(length).setTimeout(timeout).setOperationKey(controller.toString()).build();
		
		operationService.writeFlash(controller, flash, new RpcCallback<EmptyAnswer>() {
			
			@Override
			public void run(EmptyAnswer parameter) {
				if(controller.failed()){
					callback.onFailure(new Throwable(controller.errorText()));
				}
				else{
					callback.onSuccess(null);
				}
			}
		});
		
		// Rueckgabe eines OperationHandle
		return new OperationHandle<Void>(){

			// aufruf bei cancel (User-seitig)
			@Override
			public void cancel() {

				BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
				try {
					controller.startCancel();
					blockOperationService.cancelHandle(controller, OpKey.newBuilder().build()); //TODO
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}

			// aufruf bei get (User-seitig)
			@Override
			public Void get() {

				// erzeugen eines sync RPC-Objekts fuer die Operationen
		        BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
		        try {
		        	// sync RPC-Aufruf
					blockOperationService.getHandle(controller, OpKey.newBuilder().setOperationKey(controller.toString()).build());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			// aufruf bei getState (User-seitig)
			@Override
			public State getState() {
				
				// erzeugen eines State-Objekts
				State state = null;

				// Blockierender Aufruf fuer getState in Ordnung?
				
				// erzeugen eines sync RPC-Objekts fuer die Operationen
				BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
				try {
					// sync RPC-Aufruf
					STRING result = blockOperationService.getState(controller,  OpKey.newBuilder().setOperationKey(controller.toString()).build());
					// erzeugen eines State aus dem result-String
					state = State.fromName(result.getQuery());
				} catch (ServiceException e) {
					e.printStackTrace();
				}

				//alternativer async RPC aufruf
//				operationService.getState(controller, VOID.newBuilder().setHandleKey(controller.toString()).build(), new RpcCallback<STRING>(){
//					@Override
//					public void run(STRING parameter) {
//						System.out.println(parameter.getQuery());
//						state = State.fromName(parameter.getQuery());
//					}});
				
				return state;
			}};
	}

	@Override
	public OperationHandle<Void> writeMac(MacAddress macAddress, long timeout,
			final AsyncCallback<Void> callback) {

		final RpcController controller = channel.newRpcController();
		
		MacData address = MacData.newBuilder().addMACADDRESS(ByteString.copyFrom(macAddress.getMacBytes())).setTimeout(timeout).setOperationKey(controller.toString()).build();
		
		operationService.writeMac(controller, address, new RpcCallback<EmptyAnswer>() {
			
			@Override
			public void run(EmptyAnswer parameter) {
				if(controller.failed()){
					callback.onFailure(new Throwable(controller.errorText()));
				}
				else{
					callback.onSuccess(null);
				}
			}
		});
		
		// Rueckgabe eines OperationHandle
		return new OperationHandle<Void>(){

			// aufruf bei cancel (User-seitig)
			@Override
			public void cancel() {

				BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
				try {
					controller.startCancel();
					blockOperationService.cancelHandle(controller, OpKey.newBuilder().build());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}

			// aufruf bei get (User-seitig)
			@Override
			public Void get() {

				// erzeugen eines sync RPC-Objekts fuer die Operationen
		        BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
		        try {
		        	// sync RPC-Aufruf
					blockOperationService.getHandle(controller, OpKey.newBuilder().setOperationKey(controller.toString()).build());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			// aufruf bei getState (User-seitig)
			@Override
			public State getState() {
				
				// erzeugen eines State-Objekts
				State state = null;

				// Blockierender Aufruf fuer getState in Ordnung?
				
				// erzeugen eines sync RPC-Objekts fuer die Operationen
				BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
				try {
					// sync RPC-Aufruf
					STRING result = blockOperationService.getState(controller,  OpKey.newBuilder().setOperationKey(controller.toString()).build());
					// erzeugen eines State aus dem result-String
					state = State.fromName(result.getQuery());
				} catch (ServiceException e) {
					e.printStackTrace();
				}

				//alternativer async RPC aufruf
//				operationService.getState(controller, VOID.newBuilder().setHandleKey(controller.toString()).build(), new RpcCallback<STRING>(){
//					@Override
//					public void run(STRING parameter) {
//						System.out.println(parameter.getQuery());
//						state = State.fromName(parameter.getQuery());
//					}});
				
				return state;
			}};
	}

	@Override
	public OperationHandle<ChipType> getChipType(long timeout,
			AsyncCallback<ChipType> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addListener(final MessagePacketListener listener, PacketType... types) {
		
		final RpcController controller = channel.newRpcController();

		List<Integer> alist = new ArrayList<Integer>();
		for(int i=0;i<types.length;i++){
			alist.add(types[i].getValue());
		}
		
		PacketTypeData request = PacketTypeData.newBuilder().addAllType(alist).setOperationKey(listener.toString()).build();
		
		packetService.addMessagePacketListener(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(EmptyAnswer parameter) {
				packetServiceAnswerImpl.addPacketListener(listener.toString(),new MessagePacketListener() {
					@Override
					public void onMessagePacketReceived(MessageEvent<MessagePacket> event) {
						log.info("Message: " + new String(event.getMessage().getContent()));
					}
				});
			}});
		
	}

	@Override
	public void addListener(final MessagePacketListener listener, int... types) {
		
		final RpcController controller = channel.newRpcController();
		
		PacketTypeData request = PacketTypeData.newBuilder().setOperationKey(listener.toString()).build();
		
		for(int i=0;i<types.length;i++){
			request = PacketTypeData.newBuilder().setType(i, types[i]).build();
		}
		
		packetService.addMessagePacketListener(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(EmptyAnswer parameter) {
				packetServiceAnswerImpl.addPacketListener(listener.toString(),new MessagePacketListener() {
					@Override
					public void onMessagePacketReceived(MessageEvent<MessagePacket> event) {
						log.info("Message: " + new String(event.getMessage().getContent()));
					}
				});
			}});
	}

	@Override
	public void addListener(final MessagePacketListener listener) {
		
		final RpcController controller = channel.newRpcController();
		
		PacketTypeData request = PacketTypeData.newBuilder().setOperationKey(listener.toString()).build();
		
		packetService.addMessagePacketListener(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(EmptyAnswer parameter) {
				packetServiceAnswerImpl.addPacketListener(listener.toString(),new MessagePacketListener() {
					@Override
					public void onMessagePacketReceived(MessageEvent<MessagePacket> event) {
						log.info("Message: " + new String(event.getMessage().getContent()));
					}
				});
			}});
		
	}

	@Override
	public void addListener(final MessagePlainTextListener listener) {
		final RpcController controller = channel.newRpcController();
		
		PacketTypeData request = PacketTypeData.newBuilder().setOperationKey(listener.toString()).build();
		
		packetService.addMessagePlainTextListener(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(EmptyAnswer parameter) {
				packetServiceAnswerImpl.addPlainTextListener(listener.toString(), new MessagePlainTextListener() {
					@Override
					public void onMessagePlainTextReceived(
							MessageEvent<MessagePlainText> message) {
						log.info("Message: " + new String(message.getMessage().getContent()));
						
					}
				});
			}});
		
	}

	@Override
	public void removeListener(final MessagePacketListener listener) {
		
		final RpcController controller = channel.newRpcController();
		
		packetService.removeMessagePacketListener(controller, OpKey.newBuilder().setOperationKey(listener.toString()).build(), new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(EmptyAnswer parameter) {
				packetServiceAnswerImpl.removePacketListener(listener.toString());
			}});
		
	}
	
	@Override
	public void removeListener(final MessagePlainTextListener listener) {
		
		final RpcController controller = channel.newRpcController();
		
		packetService.removeMessagePlainTextListener(controller, OpKey.newBuilder().setOperationKey(listener.toString()).build(), new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(EmptyAnswer parameter) {
				packetServiceAnswerImpl.removePlainTextListener(listener.toString());
			}});
		
	}
}
	
