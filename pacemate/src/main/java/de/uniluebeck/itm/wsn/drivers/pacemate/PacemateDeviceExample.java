package de.uniluebeck.itm.wsn.drivers.pacemate;

import java.io.InputStream;

import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.util.GenericDeviceExample;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseSerialPortConnection;

public class PacemateDeviceExample extends GenericDeviceExample {
	
	public static void main(String[] args) {
		final SerialPortConnection connection = new iSenseSerialPortConnection();
		final Device<SerialPortConnection> device = new PacemateDevice(connection);
		final GenericDeviceExample example = new GenericDeviceExample();
		example.setDevice(device);
		
		final InputStream stream = PacemateDevice.class.getResourceAsStream("/de/uniluebeck/itm/rsc/drivers/pacemate/pacemate.bin");
		example.setImageInputStream(stream);

		example.setUri(args[0]);
		example.setMessage(new byte[] { 11, 17 });
		example.run();
	}
}
