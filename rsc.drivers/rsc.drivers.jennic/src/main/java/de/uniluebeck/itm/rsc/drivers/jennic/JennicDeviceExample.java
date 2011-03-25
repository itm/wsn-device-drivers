package de.uniluebeck.itm.rsc.drivers.jennic;

import java.io.File;
import java.net.URISyntaxException;

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
		
		File image = null;
		try {
			image = new File(ClassLoader.getSystemResource("de/uniluebeck/itm/rsc/drivers/jennic/jennic.bin").toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		example.setImage(image);
		
		final MessagePacket packet = new MessagePacket(11, new byte[] { 17 });
		example.setMessagePacket(packet);
		example.run();
	}

}
