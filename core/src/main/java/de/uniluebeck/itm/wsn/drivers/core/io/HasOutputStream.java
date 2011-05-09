package de.uniluebeck.itm.wsn.drivers.core.io;

import java.io.OutputStream;


/**
 * Interface to indicate that a class has an <code>OutputStream</code>.
 * 
 * @author Malte Legenhausen
 */
public interface HasOutputStream {

	/**
	 * Getter for the OutputStream.
	 * 
	 * @return The OutputStream instance.
	 */
	OutputStream getOutputStream();
}
