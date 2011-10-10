package de.uniluebeck.itm.wsn.drivers.test;

import de.uniluebeck.itm.wsn.drivers.mock.MockModule;


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
		final GenericDeviceExample example = new GenericDeviceExample();
		example.addByteReceiver(new BufferedByteReceiver());
		example.setModule(new MockModule());
		example.setMessage("This is a test message".getBytes());
		example.setUri("MockPort");
		example.run();
	}

}
