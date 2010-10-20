package de.uniluebeck.itm.devicedriver;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * Interface that defines how to manage a connection to a device.
 * 
 * @author Malte Legenhausen
 */
public interface Connection {

	/**
	 * Returns the <code>InputStream</code> of the connected iSense device.
	 * 
	 * @return The <code>InputStream</code> of the connected iSense device.
	 */
	InputStream getInputStream();
	
	/**
	 * Returns the <code>OutputStream</code> of the connected iSense device.
	 * 
	 * @return The <code>OutputStream</code> of the connected iSense device.
	 */
	OutputStream getOutputStream();
	
	/**
	 * Establish the connection with the device and return a useable device instance.
	 * 
	 * @param uri URI that identifies the resource to which a connection has to be established.
	 */
	void connect(String uri);
	
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
	
	/**
	 * Adds a listener to the connection to track connection changes.
	 * 
	 * @param listener The listener that has to be added.
	 */
	void addListener(ConnectionListener listener);
	
	/**
	 * Removes the given listener from the the internal listener list.
	 * 
	 * @param listener The listener that has to be removed.
	 */
	void removeListener(ConnectionListener listener);
}
