package de.uniluebeck.itm.wsn.drivers.factories;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.jennic.JennicDevice;
import de.uniluebeck.itm.wsn.drivers.mock.MockDevice;
import de.uniluebeck.itm.wsn.drivers.nulldevice.NullConnection;
import de.uniluebeck.itm.wsn.drivers.nulldevice.NullDevice;
import de.uniluebeck.itm.wsn.drivers.pacemate.PacemateDevice;
import de.uniluebeck.itm.wsn.drivers.telosb.TelosbDevice;

public abstract class DeviceFactory {

	public static <C extends Connection> Device<? extends Connection> create(DeviceType deviceType, C connection) {
		switch (deviceType) {
		case ISENSE:
			return new JennicDevice((SerialPortConnection) connection);
		case PACEMATE:
			return new PacemateDevice((SerialPortConnection) connection);
		case TELOSB:
			return new TelosbDevice((SerialPortConnection) connection);
		case MOCK:
			return new MockDevice(connection);
		case NULL:
			return new NullDevice((NullConnection) connection);
		}
		throw new RuntimeException("Unhandled device type \"" + deviceType
				+ "\". Maybe someone forgot to add this (new) device type to " + ConnectionFactory.class.getName()
				+ "?");
	}
	
	public static <C extends Connection> Device<? extends Connection> create(String deviceType, C connection) {
		return create(DeviceType.fromString(deviceType), connection);
	}

}
