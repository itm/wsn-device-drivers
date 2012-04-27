package de.uniluebeck.itm.wsn.drivers.core;

import java.io.IOException;

import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import de.uniluebeck.itm.wsn.drivers.core.io.HasInputStream;
import de.uniluebeck.itm.wsn.drivers.core.io.HasOutputStream;


/**
 * Interface that defines how to manage a connection to a device.
 * 
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public interface Connection extends HasInputStream, HasOutputStream, Connectable {

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
	
	/**
	 * Returns the channels on which the device is available.
	 * 
	 * @return A list of all available channels.
	 */
	int[] getChannels();
	
	/**
	 * Wait at most timeoutMillis for the input stream to become available.
	 * 
	 * @param timeoutMillis Milliseconds to wait until timeout, 0 for no timeout
	 * @return The number of characters available
	 * @throws TimeoutException when no data was available for the timeout duration.
	 * @throws IOException when something went wrong with the input stream.
	 */
	int waitDataAvailable(int timeoutMillis) throws TimeoutException, IOException;
	
	/**
	 * Skip all bytes left on the input stream.
	 * 
	 * @throws IOException
	 */
	void clear() throws IOException;
}
