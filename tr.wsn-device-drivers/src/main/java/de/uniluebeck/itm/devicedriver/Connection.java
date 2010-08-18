package de.uniluebeck.itm.devicedriver;

import gnu.io.SerialPort;

/**
 * Interface that defines how to manage a connection to a device.
 * 
 * @author Malte Legenhausen
 */
public interface Connection {

	/**
	 * Returns the <code>SerialPort</code> instance.
	 * 
	 * @return SerialPort instance.
	 */
	SerialPort getSerialPort();
	
	/**
	 * Establish the connection with the device and return a useable device instance.
	 * 
	 * @return A useable device instance.
	 */
	Device connect();
	
	/**
	 * Close the connection to the device.
	 * 
	 * @param force Shutdown without waiting for current running processes to finish.
	 */
	void shutdown(boolean force);
	
	/**
	 * Return if a connection is esablished.
	 * 
	 * @return true if there is a connection established else false.
	 */
	boolean isConnected();
	
	void addConnectionListener(ConnectionListener listener);
	
	void removeConnectionListener(ConnectionListener listener);
}
