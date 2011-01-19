package de.uniluebeck.itm.tcp.operations;

import org.apache.shiro.subject.Subject;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.tcp.Server.ClientID;


public abstract class AbstractOperation<T> implements Runnable {
	
	RpcController controller = null;
	RpcCallback<T> done = null;
	Subject user;
	ClientID id;
	DeviceAsync deviceAsync;
	
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
}
