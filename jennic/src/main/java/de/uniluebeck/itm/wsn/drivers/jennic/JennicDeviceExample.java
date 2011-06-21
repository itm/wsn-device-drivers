package de.uniluebeck.itm.wsn.drivers.jennic;

import java.io.InputStream;

import de.uniluebeck.itm.wsn.drivers.core.util.GuiceDeviceExample;
import de.uniluebeck.itm.wsn.drivers.isense.MessagePacketReceiver;


public class JennicDeviceExample {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final GuiceDeviceExample example = new GuiceDeviceExample();
		example.addByteReceiver(new MessagePacketReceiver());
		example.setModule(new JennicModule());
		example.setUri(args[0]);
		
		InputStream stream = JennicDeviceExample.class.getResourceAsStream("/de/uniluebeck/itm/wsn/drivers/jennic/jennic.bin");
		example.setImageInputStream(stream);
		example.setMessage(new byte[] { 11, 17 });
		example.run();
	}

}
