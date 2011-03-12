package de.uniluebeck.itm.devicedrivier.exmaple;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.mockdevice.MockConnection;
import de.uniluebeck.itm.devicedriver.mockdevice.MockDevice;

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
