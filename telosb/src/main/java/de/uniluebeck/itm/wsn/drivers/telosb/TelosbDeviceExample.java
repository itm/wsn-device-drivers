package de.uniluebeck.itm.wsn.drivers.telosb;

import java.io.InputStream;

import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.util.GenericDeviceExample;
import de.uniluebeck.itm.wsn.drivers.isense.MessagePacketReceiver;

public class TelosbDeviceExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final SerialPortConnection connection = new TelosbSerialPortConnection();		
		final Device<SerialPortConnection> device = new TelosbDevice(connection);
		final GenericDeviceExample example = new GenericDeviceExample();
		example.addByteReceiver(new MessagePacketReceiver());
		example.setDevice(device);
		example.setUri(args[0]);
		InputStream stream = TelosbDeviceExample.class.getResourceAsStream("/de/uniluebeck/itm/rsc/drivers/telosb/telosb.ihex");
		example.setImageInputStream(stream);
		example.setMessage(new byte[] { 11, 17 });
		example.run();
	}

}
