package de.uniluebeck.itm.tcp.server.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

import de.uniluebeck.itm.devicedriver.async.AsyncAdapter;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.tcp.server.utils.ClientID;
import de.uniluebeck.itm.tcp.server.utils.ReverseMessage;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ReverseAnswer;


public abstract class AbstractOperation<T> implements Runnable {
	
	//private static Logger log = LoggerFactory.getLogger(AbstractOperation.class);
	
	RpcController controller = null;
	RpcCallback<EmptyAnswer> done = null;
	Subject user;
	ClientID id;
	DeviceAsync deviceAsync;
	ReverseMessage message;
	
	public AbstractOperation(final RpcController controller, final RpcCallback<EmptyAnswer> done, final Subject user, final ClientID id) {
		this.controller = controller;
		this.done = done;
		this.user = user;
		this.id = id;
	}

	abstract protected void operate();
	
	@Override
	public void run(){
		if(user==null || !user.isAuthenticated()){
			controller.setFailed("Sie sind nicht authentifiziert!");
			done.run(null);
			return;
		}
		
		this.deviceAsync = id.getDevice();
		operate();
	}
	
	//public abstract void setOnSuccess(T result);
	
	public void setOnSuccess(final T result) {
		message.reverseSuccess(ReverseAnswer.newBuilder().setSuccess(OpKey.newBuilder().setOperationKey(message.getOperationKey())).build());
	}
	public void setOnExecute(){
		message.reverseExecute();
	}
	public void setOnCancel(){
		message.reverseChangeEvent("Operation was canceled by the Device",true);
	}
	public void setOnFailure(final Throwable throwable){
		message.reverseChangeEvent(throwable.getMessage(),true);
	}
	public void setOnProgressChange(final float fraction){
		message.reverseChangeEvent(String.valueOf(fraction),false);
	}
	
	public  AsyncAdapter<T> getAsyncAdapter(){
		return new AsyncAdapter<T>(){
			
			@Override
			public void onExecute() {
				setOnExecute();
			}
			
			@Override
			public void onCancel() {
				setOnCancel();
			}

			@Override
			public void onFailure(final Throwable throwable) {
				setOnFailure(throwable);
			}

			@Override
			public void onProgressChange(final float fraction) {
				setOnProgressChange(fraction);
			}
			@Override
			public void onSuccess(final T result) {
				setOnSuccess(result);
			}
		};
	}
}
