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

public class RemoteMessagePlainTextListener implements MessagePlainTextListener{

	private RpcClientChannel channel = null;
	private PacketServiceAnswer.Interface answer;
	private String OperationKey;
	
	public RemoteMessagePlainTextListener(String OperationKey, RpcClientChannel channel){
		this.OperationKey = OperationKey;
		this.channel = channel;
		answer = PacketServiceAnswer.newStub(channel);
	}

	@Override
	public void onMessagePlainTextReceived(
			final MessageEvent<MessagePlainText> message) {
		
		final RpcController controller = channel.newRpcController();
		ListenerData request = ListenerData.newBuilder().setOperationKey(OperationKey).setSource(message.getSource().toString()).addData(ByteString.copyFrom(message.getMessage().getContent())).build();

		answer.sendReversePlainTextMessage(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(EmptyAnswer parameter) {	
			}});
		
	}

}
