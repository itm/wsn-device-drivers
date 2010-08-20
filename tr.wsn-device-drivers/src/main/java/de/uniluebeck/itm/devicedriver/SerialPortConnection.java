package de.uniluebeck.itm.devicedriver;

import gnu.io.SerialPort;

/**
 * Connection type that can be used when a <code>SerialPort</code> instance is used.
 * 
 * @author Malte Legenhausen
 */
public interface SerialPortConnection extends Connection {
	
	/**
	 * Returns the <code>SerialPort</code> instance.
	 * 
	 * @return SerialPort instance.
	 */
	SerialPort getSerialPort();
}
