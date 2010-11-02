package de.uniluebeck.itm.FlashLoader;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.async.QueuedDeviceAsync;
import de.uniluebeck.itm.devicedriver.async.singlethread.SingleThreadOperationQueue;
import de.uniluebeck.itm.devicedriver.nulldevice.NullDevice;

public class FlashLoader {
	
	String port;
	String server;
	String file;
	
	public FlashLoader(){
		
	}
	
	public void setPort(String port) {
		this.port = port;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	public void flash(){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		System.out.println("File: " + file);
		
		Device device = new NullDevice();
		OperationQueue queue = new SingleThreadOperationQueue();
		DeviceAsync deviceAsync = new QueuedDeviceAsync(queue, device);
		
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
				System.out.println("Das Programmieren war erfolgreich.");
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
		OperationHandle<Void> handle = deviceAsync.program(null, 1000, callback);
		handle.get();
		
		System.exit(0);
	}
	
	public void readmac(){
		Device device = new NullDevice();
		OperationQueue queue = new SingleThreadOperationQueue();
		DeviceAsync deviceAsync = new QueuedDeviceAsync(queue, device);
		
		AsyncCallback<MacAddress> callback = new AsyncCallback<MacAddress>() {
			public void onSuccess(MacAddress result) {
				System.out.println("Mac-Adresse: " + result);
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

		OperationHandle<MacAddress> handle = deviceAsync.readMac(1000, callback);
		// Macht den Aufruf synchron.
		MacAddress address = handle.get();
		System.out.println(address);
		
		System.exit(0);
	}
	
	public void writemac(){
		Device device = new NullDevice();
		OperationQueue queue = new SingleThreadOperationQueue();
		DeviceAsync deviceAsync = new QueuedDeviceAsync(queue, device);
		
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
				System.out.println("Die Mac-Adresse wurde erfolgreich übertragen");
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

		OperationHandle<Void> handle = deviceAsync.writeMac(null, 1000, callback);
		// Macht den Aufruf synchron.
		handle.get();
		
		System.exit(0);
	}
	
	public void reset(){
		Device device = new NullDevice();
		OperationQueue queue = new SingleThreadOperationQueue();
		DeviceAsync deviceAsync = new QueuedDeviceAsync(queue, device);
		
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
				System.out.println("Das Gerät wurde neu gestartet.");
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

		OperationHandle<Void> handle = deviceAsync.reset(1000, callback);
		// Macht den Aufruf synchron.
		handle.get();
		
		System.exit(0);
	}
}
