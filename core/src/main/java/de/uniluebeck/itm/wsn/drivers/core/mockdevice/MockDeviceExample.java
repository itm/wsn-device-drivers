package de.uniluebeck.itm.wsn.drivers.core.mockdevice;

import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.util.GenericDeviceExample;


/**
 * An example program for the MockDevice.
 * 
 * @author Malte Legenhausen
 */
public class MockDeviceExample {
	
	/**
	 * Main method.
	 * 
	 * @param args Program parameters.
	 */
	public static void main(final String[] args) {
		final MockConnection connection = new MockConnection();
		final Device<MockConnection> device = new MockDevice(connection);
		final GenericDeviceExample example = new GenericDeviceExample();
		example.setDevice(device);
		example.setMessage("Hallo Welt".getBytes());
		example.run();
	}

}
