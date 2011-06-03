package de.uniluebeck.itm.wsn.drivers.jennic;

import java.io.InputStream;

import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.util.GenericDeviceExample;
import de.uniluebeck.itm.wsn.drivers.isense.MessagePacketReceiver;


public class JennicDeviceExample {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		final JennicSerialPortConnection connection = new JennicSerialPortConnection();		
		final Device<SerialPortConnection> device = new JennicDevice(connection);
		final GenericDeviceExample example = new GenericDeviceExample();
		example.addByteReceiver(new MessagePacketReceiver());
		example.setDevice(device);
		example.setUri(args[0]);
		
		InputStream stream = JennicDeviceExample.class.getResourceAsStream("/de/uniluebeck/itm/rsc/drivers/jennic/jennic.bin");
		example.setImageInputStream(stream);
		example.setMessage(new byte[] { 11, 17 });
		example.run();
	}

}
