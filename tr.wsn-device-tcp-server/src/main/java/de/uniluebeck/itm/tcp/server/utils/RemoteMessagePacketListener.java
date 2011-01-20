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

public class RemoteMessagePacketListener implements MessagePacketListener {

	private RpcClientChannel channel = null;
	private PacketServiceAnswer.Interface answer;
	private String OperationKey;
	
	public RemoteMessagePacketListener(String OperationKey, RpcClientChannel channel){
		this.OperationKey = OperationKey;
		this.channel = channel;
		answer = PacketServiceAnswer.newStub(channel);
	}

	@Override
	public void onMessagePacketReceived(final MessageEvent<MessagePacket> event) {
		
		final RpcController controller = channel.newRpcController();
		ListenerData request = ListenerData.newBuilder().setOperationKey(OperationKey).setSource(event.getSource().toString()).setType(event.getMessage().getType()).addData(ByteString.copyFrom(event.getMessage().getContent())).build();

		answer.sendReversePacketMessage(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(EmptyAnswer parameter) {	
			}});
		
	}
}
