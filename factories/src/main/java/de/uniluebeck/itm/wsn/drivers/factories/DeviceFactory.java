package de.uniluebeck.itm.wsn.drivers.factories;

import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.jennic.JennicDevice;
import de.uniluebeck.itm.wsn.drivers.pacemate.PacemateDevice;
import de.uniluebeck.itm.wsn.drivers.telosb.TelosbDevice;

public abstract class DeviceFactory {

	public static Device<SerialPortConnection> create(DeviceType deviceType, SerialPortConnection connection) {
		switch (deviceType) {
		case ISENSE:
			return new JennicDevice(connection);
		case PACEMATE:
			return new PacemateDevice(connection);
		case TELOSB:
			return new TelosbDevice(connection);
		}
		throw new RuntimeException("Unhandled device type \"" + deviceType
				+ "\". Maybe someone forgot to add this (new) device type to " + ConnectionFactory.class.getName()
				+ "?");
	}
	
	public static Device<SerialPortConnection> create(String deviceType, SerialPortConnection connection) {
		return create(DeviceType.fromString(deviceType), connection);
	}

}
