package de.uniluebeck.itm.tcp.client;


import java.util.ArrayList;
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
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.BlockingInterface;
import de.uniluebeck.itm.tcp.operations.getChipTypeOperation;
import de.uniluebeck.itm.tcp.operations.programOperation;
import de.uniluebeck.itm.tcp.operations.readMacAddressOperation;
import de.uniluebeck.itm.tcp.operations.writeFlashOperation;
import de.uniluebeck.itm.tcp.operations.writeMacOperation;

/**
 * The RemoteDevice represents one device on the server acting as a stub.
 */
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
		
		return new programOperation(channel, operationService, callback, bytes, timeout).operate();		
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
		
		return new readMacAddressOperation(channel, callback, operationService, packetServiceAnswerImpl,timeout).operate();
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
		
		return new writeFlashOperation(channel, operationService, address, data, length, timeout, callback).operate();		
	}

	@Override
	public OperationHandle<Void> writeMac(MacAddress macAddress, long timeout,
			final AsyncCallback<Void> callback) {

		return new writeMacOperation(channel, packetServiceAnswerImpl, operationService, callback, macAddress, timeout).operate();
	}

	@Override
	public OperationHandle<ChipType> getChipType(long timeout,
			final AsyncCallback<ChipType> callback) {
		
		return new getChipTypeOperation(channel, callback, operationService, packetServiceAnswerImpl, timeout).operate();
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
	
