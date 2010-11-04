package de.uniluebeck.itm.devicedriver.jennic;

import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.ConnectionListener;
import de.uniluebeck.itm.devicedriver.generic.iSenseSerialPortConnection;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;


public class JennicDeviceTest {

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.loadLibrary("rxtxSerial");
		SerialPortConnection connection = new iSenseSerialPortConnection();
		connection.addListener(new ConnectionListener() {
			
			@Override
			public void onConnectionChange(Connection connection, boolean connected) {
				if (connected) {
					System.out.println("Connection established");
				}				
			}
		});
		connection.connect("COM18");

	}

}
