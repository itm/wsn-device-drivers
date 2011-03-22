package de.uniluebeck.itm.rsc.examples.drivers;

import de.uniluebeck.itm.rsc.drivers.core.Device;
import de.uniluebeck.itm.rsc.drivers.core.MessagePacket;
import de.uniluebeck.itm.rsc.drivers.core.mockdevice.MockConnection;
import de.uniluebeck.itm.rsc.drivers.core.mockdevice.MockDevice;

public class MockDeviceExample {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final MockConnection connection = new MockConnection();
		final Device<MockConnection> device = new MockDevice(connection);
		final GenericDeviceExample example = new GenericDeviceExample();
		example.setDevice(device);
		final MessagePacket packet = new MessagePacket(0, "Hallo Welt".getBytes());
		example.setMessagePacket(packet);
		example.run();
	}

}
