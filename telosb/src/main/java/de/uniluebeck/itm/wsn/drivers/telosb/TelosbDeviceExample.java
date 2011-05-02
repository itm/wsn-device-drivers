package de.uniluebeck.itm.wsn.drivers.telosb;

import java.io.InputStream;

import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.MessagePacket;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.util.GenericDeviceExample;

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
		InputStream stream = TelosbDeviceExample.class.getResourceAsStream("/de/uniluebeck/itm/rsc/drivers/telosb/telosb.ihex");
		example.setImageInputStream(stream);
		
		final MessagePacket packet = new MessagePacket(11, new byte[] { 17 });
		example.setMessagePacket(packet);
		example.run();
	}

}
