package de.uniluebeck.itm.tcp.operations;

import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.BlockingInterface;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.Interface;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.ProgramPacket;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.STRING;

public class programOperation {
	
	RpcClientChannel channel = null;
	Operations.Interface operationService = null;
	AsyncCallback<Void> callback = null;
	final RpcController controller;
	
	byte[] bytes;
	long timeout;
	
	
	
	public programOperation(RpcClientChannel channel,
			Interface operationService, AsyncCallback<Void> callback,
			byte[] bytes, long timeout) {
		this.channel = channel;
		this.operationService = operationService;
		this.callback = callback;
		this.bytes = bytes;
		this.timeout = timeout;
		this.controller = channel.newRpcController();
	}

	public OperationHandle<Void> operate() {
		ProgramPacket packet = ProgramPacket.newBuilder().addBinaryPacket(ByteString.copyFrom(bytes)).setTimeout(timeout).setOperationKey(controller.toString()).build();
		
		operationService.program(controller, packet, new RpcCallback<EmptyAnswer>() {
			@Override
			public void run(EmptyAnswer parameter) {
				if(controller.failed()){
					callback.onFailure(new Throwable(controller.errorText()));
				}
			}
		});
		
		return getHandle();
	}
	
	public OperationHandle<Void> getHandle() {
		// Rueckgabe eines OperationHandle
		return new OperationHandle<Void>(){

			// aufruf bei cancel (User-seitig)
			@Override
			public void cancel() {
				// theoretisch soll das so gehen,
				// ist aber noch nicht getestet
				controller.startCancel();
				
				//alternativer async RPC aufruf zum abbrechen
//				operationService.cancelHandle(controller, VOID.newBuilder().build(), new RpcCallback<VOID>(){
//
//					@Override
//					public void run(VOID parameter) {
//						callback.onCancel();
//					}});
			}

			// aufruf bei get (User-seitig)
			@Override
			public Void get() {

				// erzeugen eines sync RPC-Objekts fuer die Operationen
		        BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
		        try {
		        	// sync RPC-Aufruf
					blockOperationService.getHandle(controller, OpKey.newBuilder().setOperationKey(controller.toString()).build());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			// aufruf bei getState (User-seitig)
			@Override
			public State getState() {
				
				// erzeugen eines State-Objekts
				State state = null;

				// Blockierender Aufruf fuer getState in Ordnung?
				
				// erzeugen eines sync RPC-Objekts fuer die Operationen
				BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
				try {
					// sync RPC-Aufruf
					STRING result = blockOperationService.getState(controller,  OpKey.newBuilder().setOperationKey(controller.toString()).build());
					// erzeugen eines State aus dem result-String
					state = State.fromName(result.getQuery());
				} catch (ServiceException e) {
					e.printStackTrace();
				}

				//alternativer async RPC aufruf
//				operationService.getState(controller, VOID.newBuilder().setHandleKey(controller.toString()).build(), new RpcCallback<STRING>(){
//					@Override
//					public void run(STRING parameter) {
//						System.out.println(parameter.getQuery());
//						state = State.fromName(parameter.getQuery());
//					}});
				
				return state;
			}};
	}
}
