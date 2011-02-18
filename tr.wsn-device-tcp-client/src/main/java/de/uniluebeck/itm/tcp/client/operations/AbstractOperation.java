package de.uniluebeck.itm.tcp.client.operations;

import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.tcp.client.files.PacketServiceAnswerImpl;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.GetHandleAnswers;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.STRING;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations.BlockingInterface;

public abstract class AbstractOperation<T> {

	private RpcClientChannel channel = null;
	private PacketServiceAnswerImpl packetServiceAnswerImpl = null;
	//Operations.Interface operationService = null;
	private Operations.BlockingInterface operationService = null;
	private AsyncCallback<T> callback = null;
	private final RpcController controller;
	private String operationKey;
	
	public AbstractOperation(final RpcClientChannel channel,
			final PacketServiceAnswerImpl packetServiceAnswerImpl,
			final BlockingInterface operationService, final AsyncCallback<T> callback) {
		this.channel = channel;
		this.packetServiceAnswerImpl = packetServiceAnswerImpl;
		this.operationService = operationService;
		this.callback = callback;
		this.controller = channel.newRpcController();
	}
	
	public abstract void operate() throws ServiceException;
	
	public void setOperationKey(final String operationKey) {
		this.operationKey = operationKey;
	}

	public String getOperationKey() {
		return operationKey;
	}
	
	public OperationHandle<T> execute(){
		
		try {
			operate();
			return getHandle();
		} catch (final ServiceException e) {
			callback.onFailure(new Throwable(controller.errorText()));
			packetServiceAnswerImpl.removeCallback(getOperationKey());
			return null;
		}
	}
	
	private OperationHandle<T> getHandle() {
		return new OperationHandle<T>(){

			// aufruf bei cancel (User-seitig)
			@Override
			public void cancel() {
				getCancel();
			}

			// aufruf bei get (User-seitig)
			@Override
			public T get() {
				return getGet();
			}
			
			// aufruf bei getState (User-seitig)
			@Override
			public State getState() {
				return getGetState();
			}};
	}
	
	public void getCancel(){
		final BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
		try {
        	// sync RPC-Aufruf
        	blockOperationService.cancelHandle(controller, OpKey.newBuilder().setOperationKey(controller.toString()).build());
        	controller.startCancel();
		} catch (final ServiceException e) {
			callback.onFailure(new Throwable(controller.errorText()));
		}
		packetServiceAnswerImpl.removeCallback(getOperationKey());
	}
	
	@SuppressWarnings("unchecked")
	public T getGet(){
		// erzeugen eines sync RPC-Objekts fuer die Operationen
        final BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
        try {
        	// sync RPC-Aufruf
        	final GetHandleAnswers response = blockOperationService.getHandle(controller, OpKey.newBuilder().setOperationKey(controller.toString()).build());
        	
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
        	packetServiceAnswerImpl.removeCallback(getOperationKey());
        	return obj;
        	
		} catch (final ServiceException e) {
			callback.onFailure(new Throwable(controller.errorText()));
			packetServiceAnswerImpl.removeCallback(getOperationKey());
			return null;
		}
	}
	
	public State getGetState(){
		// erzeugen eines sync RPC-Objekts fuer die Operationen
		final BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
		try {
			// sync RPC-Aufruf
			final STRING result = blockOperationService.getState(controller,  OpKey.newBuilder().setOperationKey(controller.toString()).build());
			// erzeugen eines State aus dem result-String
			packetServiceAnswerImpl.removeCallback(getOperationKey());
			return State.fromName(result.getQuery());
		} catch (final ServiceException e) {
			callback.onFailure(new Throwable(controller.errorText()));
			packetServiceAnswerImpl.removeCallback(getOperationKey());
			return null;
		}
	}

	public RpcController getController() {
		return controller;
	}

	public RpcClientChannel getChannel() {
		return channel;
	}

	public void setChannel(final RpcClientChannel channel) {
		this.channel = channel;
	}

	public PacketServiceAnswerImpl getPacketServiceAnswerImpl() {
		return packetServiceAnswerImpl;
	}

	public void setPacketServiceAnswerImpl(
			final PacketServiceAnswerImpl packetServiceAnswerImpl) {
		this.packetServiceAnswerImpl = packetServiceAnswerImpl;
	}

	public Operations.BlockingInterface getOperationService() {
		return operationService;
	}

	public void setOperationService(final Operations.BlockingInterface operationService) {
		this.operationService = operationService;
	}

	public AsyncCallback<T> getCallback() {
		return callback;
	}

	public void setCallback(final AsyncCallback<T> callback) {
		this.callback = callback;
	}
	
	
	
	

}
