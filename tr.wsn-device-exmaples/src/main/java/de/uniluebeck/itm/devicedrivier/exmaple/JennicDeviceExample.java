package de.uniluebeck.itm.devicedrivier.exmaple;

import java.io.File;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.generic.iSenseSerialPortConnection;
import de.uniluebeck.itm.devicedriver.jennic.JennicDevice;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;


public class JennicDeviceExample {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		SerialPortConnection connection = new iSenseSerialPortConnection();		
		final Device<SerialPortConnection> device = new JennicDevice(connection);
		final GenericDeviceExample example = new GenericDeviceExample();
		example.setDevice(device);
		example.setUri(args[0]);
		
		final File image = new File(ClassLoader.getSystemResource("de/uniluebeck/itm/devicedriver/example/jennic.bin").getPath());
		example.setImage(image);
		example.run();
	}

}
