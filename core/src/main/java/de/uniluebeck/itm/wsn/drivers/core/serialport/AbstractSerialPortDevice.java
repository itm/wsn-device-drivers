package de.uniluebeck.itm.wsn.drivers.core.serialport;

import de.uniluebeck.itm.wsn.drivers.core.Device;


/**
 * Abstract device that use a <code>SerialPort</code> for the connection with the device.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractSerialPortDevice implements Device<SerialPortConnection> {
	
	/**
	 * <code>SerialPortConnection</code> for this device.
	 */
	private final SerialPortConnection connection;
	
	/**
	 * Constructor.
	 * 
	 * @param connection The serial port connection for this device.
	 */
	public AbstractSerialPortDevice(final SerialPortConnection connection) {
		this.connection = connection;
	}

	@Override
	public SerialPortConnection getConnection() {
		return connection;
	}
}
