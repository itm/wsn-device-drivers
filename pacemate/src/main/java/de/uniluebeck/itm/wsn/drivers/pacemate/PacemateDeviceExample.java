package de.uniluebeck.itm.wsn.drivers.pacemate;

import java.io.InputStream;

import de.uniluebeck.itm.wsn.drivers.core.util.GenericDeviceExample;
import de.uniluebeck.itm.wsn.drivers.isense.MessagePacketReceiver;

public class PacemateDeviceExample {
	
	public static void main(String[] args) {
		final GenericDeviceExample example = new GenericDeviceExample();
		example.addByteReceiver(new MessagePacketReceiver());
		example.setModule(new PacemateModule());
		
		final InputStream stream = PacemateDeviceExample.class.getResourceAsStream("/de/uniluebeck/itm/wsn/drivers/pacemate/pacemate.bin");
		example.setImageInputStream(stream);

		example.setUri(args[0]);
		example.setMessage(new byte[] { 11, 17 });
		example.run();
	}
}
