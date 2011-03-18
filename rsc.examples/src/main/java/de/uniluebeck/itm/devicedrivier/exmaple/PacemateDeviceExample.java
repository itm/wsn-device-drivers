package de.uniluebeck.itm.devicedrivier.exmaple;

import java.io.File;

import de.uniluebeck.itm.rsc.drivers.core.Device;
import de.uniluebeck.itm.rsc.drivers.core.MessagePacket;
import de.uniluebeck.itm.rsc.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.rsc.drivers.isense.iSenseSerialPortConnection;
import de.uniluebeck.itm.rsc.drivers.pacemate.PacemateDevice;

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
