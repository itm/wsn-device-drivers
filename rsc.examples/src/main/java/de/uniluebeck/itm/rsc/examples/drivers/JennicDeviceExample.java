package de.uniluebeck.itm.rsc.examples.drivers;

import java.io.File;

import de.uniluebeck.itm.rsc.drivers.core.Device;
import de.uniluebeck.itm.rsc.drivers.core.MessagePacket;
import de.uniluebeck.itm.rsc.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.rsc.drivers.isense.iSenseSerialPortConnection;
import de.uniluebeck.itm.rsc.drivers.jennic.JennicDevice;


public class JennicDeviceExample {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		final SerialPortConnection connection = new iSenseSerialPortConnection();		
		final Device<SerialPortConnection> device = new JennicDevice(connection);
		final GenericDeviceExample example = new GenericDeviceExample();
		example.setDevice(device);
		example.setUri(args[0]);
		
		final File image = new File(ClassLoader.getSystemResource("de/uniluebeck/itm/devicedriver/example/jennic.bin").getPath());
		example.setImage(image);
		
		final MessagePacket packet = new MessagePacket(11, new byte[] { 17 });
		example.setMessagePacket(packet);
		example.run();
	}

}
