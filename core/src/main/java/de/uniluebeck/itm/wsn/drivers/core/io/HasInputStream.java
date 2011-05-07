package de.uniluebeck.itm.wsn.drivers.core.io;

import java.io.InputStream;


/**
 * Interface for getting managed input and output streams.
 * 
 * @author Malte Legenhausen
 */
public interface HasInputStream {
	
	/**
	 * Getter for an InputStream that is managed by the device.
	 * 
	 * @return A managed InputStream.
	 */
	InputStream getInputStream();
}
