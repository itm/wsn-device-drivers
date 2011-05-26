package de.uniluebeck.itm.wsn.drivers.factories;

import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseSerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.pacemate.PacemateSerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.telosb.TelosbSerialPortConnection;

@SuppressWarnings("unused")
public abstract class ConnectionFactory {

	public static SerialPortConnection create(DeviceType deviceType) {
		switch (deviceType) {
			case ISENSE:
				return new iSenseSerialPortConnection();
			case PACEMATE:
				return new PacemateSerialPortConnection();
			case TELOSB:
				return new TelosbSerialPortConnection();
		}
		throw new RuntimeException("Unhandled device type \"" + deviceType
				+ "\". Maybe someone forgot to add this (new) device type to " + ConnectionFactory.class.getName()
				+ "?"
		);
	}

	public static SerialPortConnection create(String deviceType) {
		return create(DeviceType.fromString(deviceType));
	}

}
