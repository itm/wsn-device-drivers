package de.uniluebeck.itm.wsn.drivers.core;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * Class for Getter of an Input and OutputStream.
 * 
 * @author Malte Legenhausen
 */
public interface IOContainer {

	/**
	 * Getter for an OutputStream that is managed by the device.
	 * 
	 * @return A managed OutputStream.
	 */
	OutputStream getOutputStream();
	
	/**
	 * Getter for an InputStream that is managed by the device.
	 * 
	 * @return A managed InputStream.
	 */
	InputStream getInputStream();
}
