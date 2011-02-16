package de.uniluebeck.itm.devicedrivier.exmaple;

import java.io.File;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;
import de.uniluebeck.itm.devicedriver.telosb.TelosbDevice;
import de.uniluebeck.itm.devicedriver.telosb.TelosbSerialPortConnection;

public class TelosbDeviceExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final SerialPortConnection connection = new TelosbSerialPortConnection();		
		final Device<SerialPortConnection> device = new TelosbDevice(connection);
		final GenericDeviceExample example = new GenericDeviceExample();
		example.setDevice(device);
		example.setUri(args[0]);
		
		final File image = new File(ClassLoader.getSystemResource("de/uniluebeck/itm/devicedriver/example/telosb.ihex").getPath());
		example.setImage(image);
		example.run();
	}

}
