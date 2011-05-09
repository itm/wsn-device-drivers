package de.uniluebeck.itm.wsn.drivers.core.mockdevice;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
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
		final Connection connection = new MockConnection();
		final Device<Connection> device = new MockDevice(connection);
		final GenericDeviceExample example = new GenericDeviceExample();
		example.setDevice(device);
		example.setMessage("This is a test message".getBytes());
		example.run();
	}

}
