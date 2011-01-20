package de.uniluebeck.itm.tcp.operations;

import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.GetHandleAnswers;
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
	int opKeyCounter = 0;
	
	public Operation(int counter, RpcClientChannel channel,
			PacketServiceAnswerImpl packetServiceAnswerImpl,
			Interface operationService, AsyncCallback<T> callback) {
		this.opKeyCounter = counter;
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
					//TODO vernuenftige Fehlermeldung
				}
			}

			// aufruf bei get (User-seitig)
			@SuppressWarnings("unchecked")
			@Override
			public T get() {

				//TODO get genauer anschauen
				
				// erzeugen eines sync RPC-Objekts fuer die Operationen
		        BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
		        try {
		        	// sync RPC-Aufruf
		        	GetHandleAnswers response = blockOperationService.getHandle(controller, OpKey.newBuilder().setOperationKey(controller.toString()).build());
		        	
		        	T obj = null;

		        	if(response.hasMacAddress()){
		        		obj = (T) new MacAddress(response.getMacAddress().getMACADDRESSList().get(0).toByteArray());
		        	}
		        	else if(response.hasChipData()){
		        		obj = (T) ChipType.valueOf(response.getChipData().getQuery());
		        	}
		        	else if(response.hasData()){
		        		obj = (T) response.getData().getDataList().get(0).toByteArray();
		        	}
		        	return obj;
		        	
				} catch (ServiceException e) {
					//TODO vernuenftige Fehlermeldung
					return null;
				}
			}
			
			// aufruf bei getState (User-seitig)
			@Override
			public State getState() {
				
				// erzeugen eines sync RPC-Objekts fuer die Operationen
				BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
				try {
					// sync RPC-Aufruf
					STRING result = blockOperationService.getState(controller,  OpKey.newBuilder().setOperationKey(controller.toString()).build());
					// erzeugen eines State aus dem result-String
					return State.fromName(result.getQuery());
				} catch (ServiceException e) {
					//TODO vernuenftige Fehlermeldung
					return null;
				}
			}};
	}
	
}
