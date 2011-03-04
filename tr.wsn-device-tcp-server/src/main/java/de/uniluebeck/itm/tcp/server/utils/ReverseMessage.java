package de.uniluebeck.itm.tcp.server.utils;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.FailureException;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.PacketServiceAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ReverseAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.changeMessage;

/**
 * Send the Answers from the Device to the client
 * @author Andreas Maier
 *
 */
public class ReverseMessage {

	/**
	 * RpcClientChannel
	 */
	private RpcClientChannel channel = null;
	/**
	 * Instance of PacketServiceAnswer
	 */
	private final PacketServiceAnswer.Interface answer;
	/**
	 * Instance of OperationKey
	 */
	private final String operationKey;

	/**
	 * Constructor
	 * @param operationKey the OperationKey for the ReverseMessages
	 * @param channel the channel for the ReverseMessages
	 */
	public ReverseMessage(final String operationKey, final RpcClientChannel channel){
		this.operationKey = operationKey;
		this.channel = channel;
		answer = PacketServiceAnswer.newStub(channel);
	}

	/**
	 * 
	 * @return the OperationKey for the ReverseMessages
	 */
	public String getOperationKey() {
		return operationKey;
	}
	
	/**
	 * send an onExecute-Message to the client
	 */
	public void reverseExecute(){
		
		final RpcController controller = channel.newRpcController();
		
		final OpKey request = OpKey.newBuilder().setOperationKey(operationKey).build();
		answer.reverseExecuteEvent(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {
			}});
	}
	
	/**
	 * send an onCancel-Message to the client
	 */
	public void reverseOnCancel(){
		
		final RpcController controller = channel.newRpcController();
		
		final OpKey request = OpKey.newBuilder().setOperationKey(operationKey).build();
		answer.reverseOnCancel(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {
			}});
		
	}
	
	/**
	 * send an onFailure-Message to the client
	 * @param throwable the Exception thrown from the device
	 */
	public void reverseOnFailure(final Throwable throwable){
		
		final RpcController controller = channel.newRpcController();
		
		String exceptionMessage = "";
		if(null != throwable.getMessage()){
			exceptionMessage = throwable.getMessage();
		}
		
		final FailureException request = FailureException.newBuilder().setOperationKey(operationKey).setExceptionName(throwable.getClass().getName()).setExceptionMessage(exceptionMessage).build();
		answer.reverseOnFailure(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {
			}});
		
	}
	
	/**
	 * send an onProgressChange-Message to the client
	 * @param message the message from the device
	 */
	public void reverseChangeEvent(final String message){
		
		final RpcController controller = channel.newRpcController();
		
		final changeMessage request = changeMessage.newBuilder().setOperationKey(operationKey).setQuery(message).build();
		answer.reverseChangeEvent(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {
			}});
		
	}
	
	/**
	 * send an onSuccess-Message to the client
	 * @param ans the different types of Success-Messages
	 */
	public void reverseSuccess(final ReverseAnswer ans){
		
		final RpcController controller = channel.newRpcController();
		
		answer.reverseSuccess(controller, ans, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(final EmptyAnswer parameter) {
			}});
	}
}
