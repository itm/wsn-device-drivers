package de.uniluebeck.itm.devicedrivier.exmaple;

import de.uniluebeck.itm.devicedriver.ConnectionEvent;
import de.uniluebeck.itm.devicedriver.ConnectionListener;
import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.async.AsyncAdapter;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.async.QueuedDeviceAsync;
import de.uniluebeck.itm.devicedriver.async.thread.PausableExecutorOperationQueue;
import de.uniluebeck.itm.devicedriver.generic.iSenseSerialPortConnection;
import de.uniluebeck.itm.devicedriver.jennic.JennicDevice;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;


public class JennicDeviceExample {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		SerialPortConnection connection = new iSenseSerialPortConnection();
		connection.addListener(new ConnectionListener() {
			@Override
			public void onConnectionChange(ConnectionEvent event) {
				if (event.isConnected()) {
					System.out.println("Connection established with port " + event.getUri());
				}				
			}
		});
		
		final Device device = new JennicDevice(connection);
		final OperationQueue queue = new PausableExecutorOperationQueue();
		final DeviceAsync asyncDevice = new QueuedDeviceAsync(queue, device);		
		connection.connect("COM19");
		
		final OperationHandle<MacAddress> handle1 = asyncDevice.readMac(30000, new AsyncAdapter<MacAddress>());
		System.out.println(handle1.get().toString());
		queue.shutdown(false);
		connection.shutdown(false);
		System.out.println("Shutdown");
	}

}
