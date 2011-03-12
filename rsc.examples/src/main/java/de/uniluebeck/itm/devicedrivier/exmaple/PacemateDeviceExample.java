package de.uniluebeck.itm.devicedrivier.exmaple;

import java.io.File;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.generic.iSenseSerialPortConnection;
import de.uniluebeck.itm.devicedriver.pacemate.PacemateDevice;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;

public class PacemateDeviceExample extends GenericDeviceExample {
	
	public static void main(String[] args) {
		final SerialPortConnection connection = new iSenseSerialPortConnection();
		final Device<SerialPortConnection> device = new PacemateDevice(connection);
		final GenericDeviceExample example = new GenericDeviceExample();
		example.setDevice(device);
		
		final File image = new File(ClassLoader.getSystemResource("de/uniluebeck/itm/devicedriver/example/pacemate.bin").getPath());
		example.setImage(image);
		example.setUri(args[0]);
		final MessagePacket packet = new MessagePacket(11, new byte[] { 17 });
		example.setMessagePacket(packet);
		example.run();
	}
}
