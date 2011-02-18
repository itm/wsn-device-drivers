package de.uniluebeck.itm.tcp.server.utils;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.FailureException;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.PacketServiceAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ReverseAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.clientMessage;


public class ReverseMessage {

	private RpcClientChannel channel = null;
	private final PacketServiceAnswer.Interface answer;
	private final String OperationKey;

	public ReverseMessage(final String OperationKey, final RpcClientChannel channel){
		this.OperationKey = OperationKey;
		this.channel = channel;
		answer = PacketServiceAnswer.newStub(channel);
	}

	public String getOperationKey() {
		return OperationKey;
	}
	
	public void reverseExecute(){
		
		final RpcController controller = channel.newRpcController();
		
		final OpKey request = OpKey.newBuilder().setOperationKey(OperationKey).build();
		answer.reverseExecuteEvent(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {
			}});
	}
	
	public void reverseOnCancel(){
		
		final RpcController controller = channel.newRpcController();
		
		final OpKey request = OpKey.newBuilder().setOperationKey(OperationKey).build();
		answer.reverseOnCancel(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {
			}});
		
	}
	
	public void reverseOnFailure(final Throwable throwable){
		
		final RpcController controller = channel.newRpcController();
		
		final FailureException request = FailureException.newBuilder().setOperationKey(OperationKey).setExceptionName(throwable.getClass().getName()).setExceptionMessage(throwable.getMessage()).build();
		answer.reverseOnFailure(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {
			}});
		
	}
	
	public void reverseChangeEvent(final String message){
		
		final RpcController controller = channel.newRpcController();
		
		final clientMessage request = clientMessage.newBuilder().setOperationKey(OperationKey).setQuery(message).build();
		answer.reverseChangeEvent(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {
			}});
		
	}
	
	public void reverseSuccess(final ReverseAnswer ans){
		
		final RpcController controller = channel.newRpcController();
		
		answer.reverseSuccess(controller, ans, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {
			}});
	}
}
