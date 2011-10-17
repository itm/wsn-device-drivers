package de.uniluebeck.itm.wsn.drivers.test;

import java.io.InputStream;

import de.uniluebeck.itm.wsn.drivers.core.util.GenericDeviceExample;
import de.uniluebeck.itm.wsn.drivers.telosb.TelosbModule;

public class SunspotDeviceExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final GenericDeviceExample example = new GenericDeviceExample();
		example.addByteReceiver(new MessagePacketReceiver());
		example.setModule(new TelosbModule());
		example.setUri(args[0]);
		InputStream stream = SunspotDeviceExample.class.getResourceAsStream("/de/uniluebeck/itm/wsn/drivers/telosb/telosb.ihex");
		example.setImageInputStream(stream);
		example.setMessage(new byte[] { 11, 17 });
		example.run();
	}

}
