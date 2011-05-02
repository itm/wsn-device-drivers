package de.uniluebeck.itm.rsc.drivers.jennic;

import java.io.InputStream;

import de.uniluebeck.itm.rsc.drivers.core.Device;
import de.uniluebeck.itm.rsc.drivers.core.MessagePacket;
import de.uniluebeck.itm.rsc.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.rsc.drivers.core.util.GenericDeviceExample;
import de.uniluebeck.itm.rsc.drivers.isense.iSenseSerialPortConnection;


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
		
		InputStream stream = JennicDeviceExample.class.getResourceAsStream("/de/uniluebeck/itm/rsc/drivers/jennic/jennic.bin");
		example.setImageInputStream(stream);
		
		final MessagePacket packet = new MessagePacket(11, new byte[] { 17 });
		example.setMessagePacket(packet);
		example.run();
	}

}
