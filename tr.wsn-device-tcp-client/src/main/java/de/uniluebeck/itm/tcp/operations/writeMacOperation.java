package de.uniluebeck.itm.tcp.operations;

import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.BlockingInterface;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.Interface;
import de.uniluebeck.itm.tcp.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.MacData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.STRING;

public class writeMacOperation {
	
	RpcClientChannel channel = null;
	PacketServiceAnswerImpl packetServiceAnswerImpl = null;
	Operations.Interface operationService = null;
	AsyncCallback<Void> callback = null;	
	final RpcController controller;
	
	MacAddress macAddress;
	long timeout;
	
	
	
	
	
	public writeMacOperation(RpcClientChannel channel, PacketServiceAnswerImpl packetServiceAnswerImpl, Interface operationService, AsyncCallback<Void> callback, MacAddress macAddress, long timeout) {
		this.channel = channel;
		this.packetServiceAnswerImpl = packetServiceAnswerImpl;
		this.operationService = operationService;
		this.callback = callback;
		this.controller = channel.newRpcController();
		this.macAddress = macAddress;
		this.timeout = timeout;
	}

	public OperationHandle<Void> operate() {
		
		MacData address = MacData.newBuilder().addMACADDRESS(ByteString.copyFrom(macAddress.getMacBytes())).setTimeout(timeout).setOperationKey(controller.toString()).build();
		
		packetServiceAnswerImpl.addCallback(address.getOperationKey(), callback);
		
		operationService.writeMac(controller, address, new RpcCallback<EmptyAnswer>() {
			
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

				BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
				try {
					controller.startCancel();
					blockOperationService.cancelHandle(controller, OpKey.newBuilder().build());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
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
