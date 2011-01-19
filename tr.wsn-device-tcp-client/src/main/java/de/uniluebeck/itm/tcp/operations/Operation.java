package de.uniluebeck.itm.tcp.operations;

import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.STRING;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.BlockingInterface;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.Interface;

public abstract class Operation<T> {

	RpcClientChannel channel = null;
	PacketServiceAnswerImpl packetServiceAnswerImpl = null;
	Operations.Interface operationService = null;
	AsyncCallback<T> callback = null;
	final RpcController controller;
	
	public Operation(RpcClientChannel channel,
			PacketServiceAnswerImpl packetServiceAnswerImpl,
			Interface operationService, AsyncCallback<T> callback) {
		this.channel = channel;
		this.packetServiceAnswerImpl = packetServiceAnswerImpl;
		this.operationService = operationService;
		this.callback = callback;
		this.controller = channel.newRpcController();
	}

	public abstract OperationHandle<T> operate();
	
	public OperationHandle<T> getHandle() {
		return new OperationHandle<T>(){

			// aufruf bei cancel (User-seitig)
			@Override
			public void cancel() {
				BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
				try {
		        	// sync RPC-Aufruf
		        	blockOperationService.cancelHandle(controller, OpKey.newBuilder().setOperationKey(controller.toString()).build());
		        	controller.startCancel();
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}

			// aufruf bei get (User-seitig)
			@Override
			public T get() {

				//TODO get genauer anschauen
				
				// erzeugen eines sync RPC-Objekts fuer die Operationen
		        BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
		        try {
		        	// sync RPC-Aufruf
		        	blockOperationService.getHandle(controller, OpKey.newBuilder().setOperationKey(controller.toString()).build());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return null;//TODO MacAdresse zurueckgeben
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
				
				return state;
			}};
	}
	
}
