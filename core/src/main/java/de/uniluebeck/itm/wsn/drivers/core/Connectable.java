package de.uniluebeck.itm.wsn.drivers.core;

import java.io.Closeable;
import java.io.IOException;


/**
 * The corresponding interface to <code>Closeable</code>.
 * 
 * @author Malte Legenhausen
 */
public interface Connectable extends Closeable {

	/**
	 * Establish the connection with the device and return a useable device instance.
	 * 
	 * @param uri URI that identifies the resource to which a connection has to be established.
	 */
	void connect(String uri) throws IOException;
	
	/**
	 * Return if a connection is established.
	 * 
	 * @return true if there is a connection established else false.
	 */
	boolean isConnected();
	
	/**
	 * Returns true if the connection is closed else false.
	 * 
	 * @return true if the connection is closed else false.
	 */
	boolean isClosed();
}
