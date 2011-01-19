package de.uniluebeck.itm.tcp.Server;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.tcp.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.PacketServiceAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.ReverseAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.clientMessage;;

public class ReverseMessage {

	private RpcClientChannel channel = null;
	private PacketServiceAnswer.Interface answer;
	private String OperationKey;
	
	public ReverseMessage(String OperationKey, RpcClientChannel channel){
		this.OperationKey = OperationKey;
		this.channel = channel;
		answer = PacketServiceAnswer.newStub(channel);
	}

	
	public void reverseProgressChange(String message){
		
		final RpcController controller = channel.newRpcController();
		clientMessage request = clientMessage.newBuilder().setOperationKey(OperationKey).setQuery(message).build();
		answer.reverseProgressChange(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(EmptyAnswer parameter) {
			}});
		
	}
	
	public void reverseSuccess(ReverseAnswer ans){
		
		final RpcController controller = channel.newRpcController();
		
		answer.reverseSuccess(controller, ans, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(EmptyAnswer parameter) {
			}});
	}
}
