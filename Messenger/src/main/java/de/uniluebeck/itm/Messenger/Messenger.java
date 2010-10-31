package de.uniluebeck.itm.Messenger;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.async.QueuedDeviceAsync;
import de.uniluebeck.itm.devicedriver.async.singlethread.SingleThreadOperationQueue;
import de.uniluebeck.itm.devicedriver.nulldevice.NullDevice;

public class Messenger {
	
	String port;
	String server;
	
	public Messenger(){

	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setServer(String server) {
		this.server = server;
	}
	
	public void send(String message){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		System.out.println("Message: " + message);
		
		Device device = new NullDevice();
		OperationQueue queue = new SingleThreadOperationQueue();
		DeviceAsync deviceAsync = new QueuedDeviceAsync(queue, device);
		
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
				System.out.println("Die Nachricht wurde verschickt");
			}
			public void onCancel() {
				System.out.println("Die Operation wurde abgebrochen");
			}
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
			public void onProgressChange(float fraction) {
				System.out.println("Es tut sich was.");
			}
		};

		OperationHandle<Void> handle = deviceAsync.send(null, 1000, callback);
		handle.get();
		
		System.exit(0);
	}
}
