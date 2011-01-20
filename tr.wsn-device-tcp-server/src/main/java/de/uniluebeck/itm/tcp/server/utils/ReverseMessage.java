package de.uniluebeck.itm.tcp.server.utils;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.PacketServiceAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ReverseAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.clientMessage;


public class ReverseMessage {

	private RpcClientChannel channel = null;
	private PacketServiceAnswer.Interface answer;
	private String OperationKey;

	public ReverseMessage(String OperationKey, RpcClientChannel channel){
		this.OperationKey = OperationKey;
		this.channel = channel;
		answer = PacketServiceAnswer.newStub(channel);
	}

	public String getOperationKey() {
		return OperationKey;
	}
	
	public void reverseExecute(){
		
		final RpcController controller = channel.newRpcController();
		
		OpKey request = OpKey.newBuilder().setOperationKey(OperationKey).build();
		answer.reverseExecuteEvent(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(EmptyAnswer parameter) {
			}});
	}
	
	public void reverseChangeEvent(String message, boolean failed){
		
		final RpcController controller = channel.newRpcController();
		
		if (failed){
			controller.setFailed(message);
		}
		
		clientMessage request = clientMessage.newBuilder().setOperationKey(OperationKey).setQuery(message).build();
		answer.reverseChangeEvent(controller, request, new RpcCallback<EmptyAnswer>(){

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
