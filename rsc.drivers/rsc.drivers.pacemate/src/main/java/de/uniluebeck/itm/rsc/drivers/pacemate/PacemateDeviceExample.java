package de.uniluebeck.itm.rsc.drivers.pacemate;

import java.io.InputStream;

import de.uniluebeck.itm.rsc.drivers.core.Device;
import de.uniluebeck.itm.rsc.drivers.core.MessagePacket;
import de.uniluebeck.itm.rsc.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.rsc.drivers.core.util.GenericDeviceExample;
import de.uniluebeck.itm.rsc.drivers.isense.iSenseSerialPortConnection;

public class PacemateDeviceExample extends GenericDeviceExample {
	
	public static void main(String[] args) {
		final SerialPortConnection connection = new iSenseSerialPortConnection();
		final Device<SerialPortConnection> device = new PacemateDevice(connection);
		final GenericDeviceExample example = new GenericDeviceExample();
		example.setDevice(device);
		
		final InputStream stream = PacemateDevice.class.getResourceAsStream("/de/uniluebeck/itm/rsc/drivers/pacemate/pacemate.bin");
		example.setImageInputStream(stream);

		example.setUri(args[0]);
		final MessagePacket packet = new MessagePacket(11, new byte[] { 17 });
		example.setMessagePacket(packet);
		example.run();
	}
}
