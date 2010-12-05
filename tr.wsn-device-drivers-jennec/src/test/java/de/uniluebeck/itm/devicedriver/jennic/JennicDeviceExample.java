package de.uniluebeck.itm.devicedriver.jennic;

import de.uniluebeck.itm.devicedriver.ConnectionEvent;
import de.uniluebeck.itm.devicedriver.ConnectionListener;
import de.uniluebeck.itm.devicedriver.generic.iSenseSerialPortConnection;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;


public class JennicDeviceExample {

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.loadLibrary("rxtxSerial");
		SerialPortConnection connection = new iSenseSerialPortConnection();
		connection.addListener(new ConnectionListener() {
			
			@Override
			public void onConnectionChange(ConnectionEvent event) {
				if (event.isConnected()) {
					System.out.println("Connection established");
				}				
			}
		});
		connection.connect("COM18");
	}

}
