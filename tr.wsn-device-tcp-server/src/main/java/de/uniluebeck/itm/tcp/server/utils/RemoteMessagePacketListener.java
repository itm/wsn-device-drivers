package de.uniluebeck.itm.tcp.server.utils;


import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ListenerData;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.PacketServiceAnswer;

/**
 * Class for sending a MessagePacket back to the client
 * @author Andreas Maier
 *
 */
public class RemoteMessagePacketListener implements MessagePacketListener {

	/**
	 * client Channel
	 */
	private RpcClientChannel channel = null;
	
	/**
	 * Rpc-Pro Interface for the Answer
	 */
	private final PacketServiceAnswer.Interface answer;
	
	/**
	 * key for the Listener
	 */
	private final String operationKey;
	
	/**
	 * Constructor
	 * @param operationKey key for the Listener 
	 * @param channel channel for the client
	 */
	public RemoteMessagePacketListener(final String operationKey, final RpcClientChannel channel){
		this.operationKey = operationKey;
		this.channel = channel;
		answer = PacketServiceAnswer.newStub(channel);
	}

	@Override
	public void onMessagePacketReceived(final MessageEvent<MessagePacket> event) {
		
		final RpcController controller = channel.newRpcController();
		final ListenerData request = ListenerData.newBuilder().setOperationKey(operationKey).setSource(event.getSource().toString()).setType(event.getMessage().getType()).addData(ByteString.copyFrom(event.getMessage().getContent())).build();

		answer.sendReversePacketMessage(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {	
			}});
		
	}
}
