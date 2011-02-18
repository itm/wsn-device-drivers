package de.uniluebeck.itm.tcp.client;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
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
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;
import de.uniluebeck.itm.tcp.client.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.PacketService;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.PacketServiceAnswer;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.PacketTypeData;
import de.uniluebeck.itm.tcp.client.operations.eraseFlashOperation;
import de.uniluebeck.itm.tcp.client.operations.getChipTypeOperation;
import de.uniluebeck.itm.tcp.client.operations.programOperation;
import de.uniluebeck.itm.tcp.client.operations.readFlashOperation;
import de.uniluebeck.itm.tcp.client.operations.readMacAddressOperation;
import de.uniluebeck.itm.tcp.client.operations.resetOperation;
import de.uniluebeck.itm.tcp.client.operations.sendOperation;
import de.uniluebeck.itm.tcp.client.operations.writeFlashOperation;
import de.uniluebeck.itm.tcp.client.operations.writeMacOperation;

/**
 * The RemoteDevice represents one device on the server acting as a stub.
 * @author Schuett
 */
public class RemoteDevice implements DeviceAsync{

	private static Logger log = LoggerFactory.getLogger(RemoteDevice.class);
	
	private ThreadPoolCallExecutor executor = null;
	private DuplexTcpClientBootstrap bootstrap = null;
	private RpcClientChannel channel = null;
	//Operations.Interface operationService = null;
	private Operations.BlockingInterface operationService = null;
	private PacketService.Interface packetService = null;
	private PacketServiceAnswerImpl packetServiceAnswerImpl = null;
	private RemoteConnection connection = null;

	public RemoteDevice(final RemoteConnection connection){
		this.connection = connection;
		this.channel = connection.getChannel();
		this.bootstrap = connection.getBootstrap();
		//operationService = Operations.newStub(channel);
		operationService = Operations.newBlockingStub(channel);
		packetService = PacketService.newStub(channel);
		packetServiceAnswerImpl = new PacketServiceAnswerImpl();
		
		// registrieren der Reverse-RPC Services
		bootstrap.getRpcServiceRegistry().registerService(PacketServiceAnswer.newReflectiveService(packetServiceAnswerImpl));

	}

	@Override
	public OperationHandle<Void> program(final byte[] bytes,
			final long timeout, final AsyncCallback<Void> callback) {
		
		return new programOperation(channel, callback, operationService, packetServiceAnswerImpl, bytes, timeout).execute();		
	}

	@Override
	public OperationHandle<Void> eraseFlash(final long timeout,
			final AsyncCallback<Void> callback) {
		
		return new eraseFlashOperation(channel, callback, operationService, packetServiceAnswerImpl, timeout).execute();
	}

	@Override
	public OperationHandle<byte[]> readFlash(final int address, final int length,
			final long timeout, final AsyncCallback<byte[]> callback) {

		return new readFlashOperation(channel, callback, operationService, packetServiceAnswerImpl, address, length, timeout).execute();
	}

	@Override
	public OperationHandle<MacAddress> readMac(final long timeout,
			final AsyncCallback<MacAddress> callback) {
		
		return new readMacAddressOperation(channel, callback, operationService, packetServiceAnswerImpl,timeout).execute();
	}

	@Override
	public OperationHandle<Void> reset(final long timeout,
			final AsyncCallback<Void> callback) {

		return new resetOperation(channel, callback, operationService, packetServiceAnswerImpl, timeout).execute();
	}

	@Override
	public OperationHandle<Void> send(final MessagePacket packet, final long timeout,
			final AsyncCallback<Void> callback) {

		return new sendOperation(channel, callback, operationService, packetServiceAnswerImpl, packet, timeout).execute();
	}

	@Override
	public OperationHandle<Void> writeFlash(final int address, final byte[] data,
			final int length, final long timeout, final AsyncCallback<Void> callback) {
		
		return new writeFlashOperation(channel, callback, operationService, packetServiceAnswerImpl,address,data,length,timeout).execute();		
	}

	@Override
	public OperationHandle<Void> writeMac(final MacAddress macAddress, final long timeout,
			final AsyncCallback<Void> callback) {

		return new writeMacOperation(channel, packetServiceAnswerImpl, operationService, callback, macAddress, timeout).execute();
	}

	@Override
	public OperationHandle<ChipType> getChipType(final long timeout,
			final AsyncCallback<ChipType> callback) {
		
		return new getChipTypeOperation(channel, callback, operationService, packetServiceAnswerImpl, timeout).execute();
	}

	@Override
	public void addListener(final MessagePacketListener listener, final PacketType... types) {
		
		final RpcController controller = channel.newRpcController();

		final List<Integer> alist = new ArrayList<Integer>();
		for(int i=0;i<types.length;i++){
			alist.add(types[i].getValue());
		}
		
		final PacketTypeData request = PacketTypeData.newBuilder().addAllType(alist).setOperationKey(listener.toString()).build();
		
		packetService.addMessagePacketListener(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {
				packetServiceAnswerImpl.addPacketListener(listener.toString(),new MessagePacketListener() {
					@Override
					public void onMessagePacketReceived(final MessageEvent<MessagePacket> event) {
						listener.onMessagePacketReceived(event);
						//log.info("Message: " + new String(event.getMessage().getContent()));
					}
				});
			}});
		
	}

	@Override
	public void addListener(final MessagePacketListener listener, final int... types) {
		
		final RpcController controller = channel.newRpcController();
		
		PacketTypeData request = PacketTypeData.newBuilder().setOperationKey(listener.toString()).build();
		
		for(int i=0;i<types.length;i++){
			request = PacketTypeData.newBuilder().setType(i, types[i]).build();
		}
		
		packetService.addMessagePacketListener(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {
				packetServiceAnswerImpl.addPacketListener(listener.toString(),new MessagePacketListener() {
					@Override
					public void onMessagePacketReceived(final MessageEvent<MessagePacket> event) {
						listener.onMessagePacketReceived(event);
						//log.info("Message: " + new String(event.getMessage().getContent()));
					}
				});
			}});
	}

	@Override
	public void addListener(final MessagePacketListener listener) {
		
		final RpcController controller = channel.newRpcController();
		
		final PacketTypeData request = PacketTypeData.newBuilder().setOperationKey(listener.toString()).build();
		
		packetService.addMessagePacketListener(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {
				packetServiceAnswerImpl.addPacketListener(listener.toString(),new MessagePacketListener() {
					@Override
					public void onMessagePacketReceived(final MessageEvent<MessagePacket> event) {
						log.info("Message: " + new String(event.getMessage().getContent()));
					}
				});
			}});
		
	}

	@Override
	public void addListener(final MessagePlainTextListener listener) {
		final RpcController controller = channel.newRpcController();
		
		final PacketTypeData request = PacketTypeData.newBuilder().setOperationKey(listener.toString()).build();
		
		packetService.addMessagePlainTextListener(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {
				packetServiceAnswerImpl.addPlainTextListener(listener.toString(), new MessagePlainTextListener() {
					@Override
					public void onMessagePlainTextReceived(
							final MessageEvent<MessagePlainText> message) {
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
			public void run(final EmptyAnswer parameter) {
				packetServiceAnswerImpl.removePacketListener(listener.toString());
			}});
		
	}
	
	@Override
	public void removeListener(final MessagePlainTextListener listener) {
		
		final RpcController controller = channel.newRpcController();
		
		packetService.removeMessagePlainTextListener(controller, OpKey.newBuilder().setOperationKey(listener.toString()).build(), new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {
				packetServiceAnswerImpl.removePlainTextListener(listener.toString());
			}});
		
	}
}
	
