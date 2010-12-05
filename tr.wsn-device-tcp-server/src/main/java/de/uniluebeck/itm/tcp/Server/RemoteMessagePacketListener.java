package de.uniluebeck.itm.tcp.Server;


import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.MessagePlainText;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.PacketServiceAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.VOID;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.sendData;

public class RemoteMessagePacketListener implements MessagePacketListener {

	RpcClientChannel channel = null;
	String key = null;
	
	RemoteMessagePacketListener(String key, RpcClientChannel channel){
		this.key = key;
		this.channel = channel;
	}
	
	@Override
	public void onMessagePacketReceived(MessagePacket message) {

		PacketServiceAnswer.Interface answer = PacketServiceAnswer.newStub(channel);
		final RpcController controller = channel.newRpcController();
		sendData request = sendData.newBuilder().setOperationKey(key).setType(message.getType()).addData(ByteString.copyFrom(message.getContent())).build();

		answer.sendReverseMessage(controller, request, new RpcCallback<VOID>(){

			@Override
			public void run(VOID parameter) {	
			}});
	}

	@Override
	public void onMessagePlainTextReceived(MessagePlainText message) {
		
		PacketServiceAnswer.Interface answer = PacketServiceAnswer.newStub(channel);
		final RpcController controller = channel.newRpcController();
		sendData request = sendData.newBuilder().setOperationKey(key).addData(ByteString.copyFrom(message.getContent())).build();

		answer.sendReverseMessage(controller, request, new RpcCallback<VOID>(){

			@Override
			public void run(VOID parameter) {	
			}});
	}

}
