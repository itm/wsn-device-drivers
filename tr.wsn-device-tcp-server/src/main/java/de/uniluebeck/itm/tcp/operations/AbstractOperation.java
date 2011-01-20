package de.uniluebeck.itm.tcp.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

import de.uniluebeck.itm.devicedriver.async.AsyncAdapter;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.tcp.Server.ClientID;
import de.uniluebeck.itm.tcp.Server.ReverseMessage;


public abstract class AbstractOperation<T,R> implements Runnable {
	
	//private static Logger log = LoggerFactory.getLogger(AbstractOperation.class);
	
	RpcController controller = null;
	RpcCallback<T> done = null;
	Subject user;
	ClientID id;
	DeviceAsync deviceAsync;
	ReverseMessage message;
	
	public AbstractOperation(RpcController controller, RpcCallback<T> done, Subject user, ClientID id) {
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
	
	public abstract void setOnSuccess(R result);
	
	public void setOnExecute(){
		message.reverseExecute();
	}
	public void setOnCancel(){
		message.reverseChangeEvent("Operation was canceled by the Device",true);
	}
	public void setOnFailure(Throwable throwable){
		message.reverseChangeEvent(throwable.getMessage(),true);
	}
	public void setOnProgressChange(float fraction){
		message.reverseChangeEvent(String.valueOf(fraction),false);
	}
	
	public  AsyncAdapter<R> getAsyncAdapter(){
		return new AsyncAdapter<R>(){
			
			@Override
			public void onExecute() {
				setOnExecute();
			}
			
			@Override
			public void onCancel() {
				setOnCancel();
			}

			@Override
			public void onFailure(Throwable throwable) {
				setOnFailure(throwable);
			}

			@Override
			public void onProgressChange(float fraction) {
				setOnProgressChange(fraction);
			}
			@Override
			public void onSuccess(R result) {
				setOnSuccess(result);
			}
		};
	}
}
