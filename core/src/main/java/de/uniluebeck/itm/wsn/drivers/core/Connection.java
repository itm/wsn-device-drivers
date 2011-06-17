package de.uniluebeck.itm.wsn.drivers.core;

import java.io.Closeable;

import de.uniluebeck.itm.wsn.drivers.core.io.HasInputStream;
import de.uniluebeck.itm.wsn.drivers.core.io.HasOutputStream;


/**
 * Interface that defines how to manage a connection to a device.
 * 
 * @author Malte Legenhausen
 */
public interface Connection extends HasInputStream, HasOutputStream, Closeable {
	
	/**
	 * Establish the connection with the device and return a useable device instance.
	 * 
	 * @param uri URI that identifies the resource to which a connection has to be established.
	 */
	void connect(String uri);
	
	/**
	 * Return if a connection is established.
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

	void addListener(DataAvailableListener listener);

	void removeListener(DataAvailableListener listener);
}
