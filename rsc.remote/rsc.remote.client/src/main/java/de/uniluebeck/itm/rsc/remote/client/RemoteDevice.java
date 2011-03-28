package de.uniluebeck.itm.rsc.remote.client;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientBootstrap;

import de.uniluebeck.itm.rsc.drivers.core.ChipType;
import de.uniluebeck.itm.rsc.drivers.core.MacAddress;
import de.uniluebeck.itm.rsc.drivers.core.MessagePacket;
import de.uniluebeck.itm.rsc.drivers.core.MessagePacketListener;
import de.uniluebeck.itm.rsc.drivers.core.MessagePlainText;
import de.uniluebeck.itm.rsc.drivers.core.MessagePlainTextListener;
import de.uniluebeck.itm.rsc.drivers.core.PacketType;
import de.uniluebeck.itm.rsc.drivers.core.async.AsyncCallback;
import de.uniluebeck.itm.rsc.drivers.core.async.DeviceAsync;
import de.uniluebeck.itm.rsc.drivers.core.async.OperationHandle;
import de.uniluebeck.itm.rsc.drivers.core.event.MessageEvent;
import de.uniluebeck.itm.rsc.remote.client.operations.EraseFlashOperation;
import de.uniluebeck.itm.rsc.remote.client.operations.GetChipTypeOperation;
import de.uniluebeck.itm.rsc.remote.client.operations.ProgramOperation;
import de.uniluebeck.itm.rsc.remote.client.operations.ReadFlashOperation;
import de.uniluebeck.itm.rsc.remote.client.operations.ReadMacAddressOperation;
import de.uniluebeck.itm.rsc.remote.client.operations.ResetOperation;
import de.uniluebeck.itm.rsc.remote.client.operations.SendOperation;
import de.uniluebeck.itm.rsc.remote.client.operations.WriteFlashOperation;
import de.uniluebeck.itm.rsc.remote.client.operations.WriteMacOperation;
import de.uniluebeck.itm.rsc.remote.client.utils.PacketServiceAnswerImpl;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.Operations;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.PacketService;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.PacketServiceAnswer;
import de.uniluebeck.itm.rsc.remote.files.MessageServiceFiles.PacketTypeData;

/**
 * The RemoteDevice represents one device on the server acting as a stub.
 * @author Schuett
 */
public class RemoteDevice implements DeviceAsync{

	/**
	 * Logger
	 */
	private static Logger log = LoggerFactory.getLogger(RemoteDevice.class);

	/**
	 * netty DuplexTcpClientBootstrap
	 */
	private DuplexTcpClientBootstrap bootstrap = null;
	/**
	 * RpcClientChannel
	 */
	private RpcClientChannel channel = null;
	/**
	 * BlockingInterface of Operations
	 */
	private Operations.BlockingInterface operationService = null;
	/**
	 * non-blocking Interface of PacketService
	 */
	private PacketService.Interface packetService = null;
	/**
	 * Instance of PacketServiceAnswerImpl
	 */
	private PacketServiceAnswerImpl packetServiceAnswerImpl = null;

	/**
	 * constructor
	 * @param connection the RemoteConnection which should be used for this device
	 */
	public RemoteDevice(final RemoteConnection connection){
		this.channel = connection.getChannel();
		this.bootstrap = connection.getBootstrap();
		operationService = Operations.newBlockingStub(channel);
		packetService = PacketService.newStub(channel);
		packetServiceAnswerImpl = new PacketServiceAnswerImpl();
		
		// registrieren der Reverse-RPC Services
		bootstrap.getRpcServiceRegistry().registerService(PacketServiceAnswer.newReflectiveService(packetServiceAnswerImpl));

	}

	@Override
	public OperationHandle<Void> program(final byte[] bytes,
			final long timeout, final AsyncCallback<Void> callback) {
		
		return new ProgramOperation(channel, callback, operationService, packetServiceAnswerImpl, bytes, timeout).execute();		
	}

	@Override
	public OperationHandle<Void> eraseFlash(final long timeout,
			final AsyncCallback<Void> callback) {
		
		return new EraseFlashOperation(channel, callback, operationService, packetServiceAnswerImpl, timeout).execute();
	}

	@Override
	public OperationHandle<byte[]> readFlash(final int address, final int length,
			final long timeout, final AsyncCallback<byte[]> callback) {

		return new ReadFlashOperation(channel, callback, operationService, packetServiceAnswerImpl, address, length, timeout).execute();
	}
	
	@Override
	public OperationHandle<MacAddress> readMac(final long timeout,
			final AsyncCallback<MacAddress> callback) {
		
		return new ReadMacAddressOperation(channel, callback, operationService, packetServiceAnswerImpl,timeout).execute();
	}

	@Override
	public OperationHandle<Void> reset(final long timeout,
			final AsyncCallback<Void> callback) {

		return new ResetOperation(channel, callback, operationService, packetServiceAnswerImpl, timeout).execute();
	}

	@Override
	public OperationHandle<Void> send(final MessagePacket packet, final long timeout,
			final AsyncCallback<Void> callback) {

		return new SendOperation(channel, callback, operationService, packetServiceAnswerImpl, packet, timeout).execute();
	}

	@Override
	public OperationHandle<Void> writeFlash(final int address, final byte[] data,
			final int length, final long timeout, final AsyncCallback<Void> callback) {
		
		return new WriteFlashOperation(channel, callback, operationService, packetServiceAnswerImpl,address,data,length,timeout).execute();		
	}

	@Override
	public OperationHandle<Void> writeMac(final MacAddress macAddress, final long timeout,
			final AsyncCallback<Void> callback) {

		return new WriteMacOperation(channel, packetServiceAnswerImpl, operationService, callback, macAddress, timeout).execute();
	}

	@Override
	public OperationHandle<ChipType> getChipType(final long timeout,
			final AsyncCallback<ChipType> callback) {
		
		return new GetChipTypeOperation(channel, callback, operationService, packetServiceAnswerImpl, timeout).execute();
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
	
