package de.uniluebeck.itm.wsn.drivers.core.io;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * Interface for getting managed input and output streams.
 * 
 * @author Malte Legenhausen
 */
public interface HasManagedStreams {

	/**
	 * Getter for an OutputStream that is managed by the device.
	 * 
	 * @return A managed OutputStream.
	 */
	OutputStream getManagedOutputStream();
	
	/**
	 * Getter for an InputStream that is managed by the device.
	 * 
	 * @return A managed InputStream.
	 */
	InputStream getManagedInputStream();
}
