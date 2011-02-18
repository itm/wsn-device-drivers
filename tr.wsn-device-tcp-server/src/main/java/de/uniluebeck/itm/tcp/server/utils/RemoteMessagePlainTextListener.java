package de.uniluebeck.itm.tcp.server.utils;


import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.MessagePlainText;
import de.uniluebeck.itm.devicedriver.MessagePlainTextListener;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ListenerData;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.PacketServiceAnswer;

/**
 * Class for sending a PlainMessagePacket back to the client
 * @author Andreas Maier
 *
 */
public class RemoteMessagePlainTextListener implements MessagePlainTextListener{
	
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
	public RemoteMessagePlainTextListener(final String operationKey, final RpcClientChannel channel){
		this.operationKey = operationKey;
		this.channel = channel;
		answer = PacketServiceAnswer.newStub(channel);
	}

	@Override
	public void onMessagePlainTextReceived(
			final MessageEvent<MessagePlainText> message) {
		
		final RpcController controller = channel.newRpcController();
		final ListenerData request = ListenerData.newBuilder().setOperationKey(operationKey).setSource(message.getSource().toString()).addData(ByteString.copyFrom(message.getMessage().getContent())).build();

		answer.sendReversePlainTextMessage(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {	
			}});
		
	}

}
