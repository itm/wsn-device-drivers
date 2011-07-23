package de.uniluebeck.itm.wsn.drivers.core;

import java.io.Closeable;
import java.io.IOException;

import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
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
	
	int[] getChannels();
	
	
	/**
	 * Wait at most timeoutMillis for the input stream to become available.
	 * 
	 * @param timeout Milliseconds to wait until timeout, 0 for no timeout
	 * @return The number of characters available
	 * @throws TimeoutException when no data was available for the timeout duration.
	 * @throws IOException when something went wrong with the input stream.
	 */
	int waitDataAvailable(int timeout) throws TimeoutException, IOException;
	
	/**
	 * Skip all bytes left on the input stream.
	 * 
	 * @throws IOException
	 */
	void clear() throws IOException;
}
