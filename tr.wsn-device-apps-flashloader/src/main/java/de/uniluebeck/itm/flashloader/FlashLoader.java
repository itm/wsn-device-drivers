package de.uniluebeck.itm.flashloader;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.async.AsyncAdapter;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.async.QueuedDeviceAsync;
import de.uniluebeck.itm.devicedriver.async.singlethread.SingleThreadOperationQueue;
import de.uniluebeck.itm.devicedriver.mockdevice.MockConnection;
import de.uniluebeck.itm.devicedriver.mockdevice.MockDevice;

public class FlashLoader {
	
	String port;
	String server;
	
	public FlashLoader(){
		
	}
	
	public void setPort(String port) {
		this.port = port;
	}

	public void setServer(String server) {
		this.server = server;
	}
	
	public void flash(String file){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		System.out.println("File: " + file);
		
		final OperationQueue queue = new SingleThreadOperationQueue();
		final MockConnection connection = new MockConnection();
		final Device device = new MockDevice(connection);
		final DeviceAsync deviceAsync = new QueuedDeviceAsync(queue, device);
		
		connection.connect("MockPort");
		System.out.println("Connected");
		
		System.out.println("Program the Device");
		deviceAsync.program(file.getBytes(), 10000, new AsyncAdapter<Void>() {

			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Programming the Device: " + percent + "%");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("The Device has been flashed.");
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		
		System.exit(0);
	}
	
	public void readmac(){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		
		final OperationQueue queue = new SingleThreadOperationQueue();
		final MockConnection connection = new MockConnection();
		final Device device = new MockDevice(connection);
		final DeviceAsync deviceAsync = new QueuedDeviceAsync(queue, device);
		
		connection.connect("MockPort");
		System.out.println("Connected");
		
		System.out.println("Reading mac address...");
		
		final AsyncCallback<MacAddress> callback = new AsyncAdapter<MacAddress>() {
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reading mac address progress: " + percent + "%");
			}
			
			public void onSuccess(MacAddress result) {
				System.out.println("Mac Address: " + result.getMacString());
			}
			
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		};
		
		deviceAsync.readMac(10000, callback);
		
		System.exit(0);
	}
	
	public void writemac(MacAddress macAdresse){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		
		final OperationQueue queue = new SingleThreadOperationQueue();
		final MockConnection connection = new MockConnection();
		final Device device = new MockDevice(connection);
		final DeviceAsync deviceAsync = new QueuedDeviceAsync(queue, device);
		
		connection.connect("MockPort");
		System.out.println("Connected");
		
		System.out.println("Setting Mac Address");
		deviceAsync.writeMac(new MacAddress(1024), 10000, new AsyncAdapter<Void>() {

			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Writing mac address progress: " + percent + "%");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("Mac Address written");
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		
		System.exit(0);
	}
	
	public void reset(){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		
		final OperationQueue queue = new SingleThreadOperationQueue();
		final MockConnection connection = new MockConnection();
		final Device device = new MockDevice(connection);
		final DeviceAsync deviceAsync = new QueuedDeviceAsync(queue, device);
		
		connection.connect("MockPort");
		System.out.println("Connected");
		
		System.out.println("Reset");
		deviceAsync.reset(10000, new AsyncAdapter<Void>() {

			@Override
			public void onProgressChange(float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reset the Device: " + percent + "%");
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("Device has been reseted");
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		
		System.exit(0);
	}
}