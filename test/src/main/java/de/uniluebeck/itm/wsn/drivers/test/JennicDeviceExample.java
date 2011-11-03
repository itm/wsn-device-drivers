package de.uniluebeck.itm.wsn.drivers.test;

import de.uniluebeck.itm.wsn.drivers.core.util.GenericDeviceExample;
import de.uniluebeck.itm.wsn.drivers.jennic.JennicModule;

import java.io.InputStream;


public class JennicDeviceExample {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final GenericDeviceExample example = new GenericDeviceExample();
		example.addByteReceiver(new MessagePacketReceiver());
		example.setModule(new JennicModule());
		example.setUri(args[0]);
		
		InputStream stream = JennicDeviceExample.class.getResourceAsStream("/de/uniluebeck/itm/wsn/drivers/jennic/jennic.bin");
		example.setImageInputStream(stream);
		example.setMessage(new byte[] { 11, 17 });
		example.run();
	}

}
