package de.uniluebeck.itm.wsn.drivers.core.exception;

import java.io.IOException;


/**
 * Exception is thrown when no port is available.
 * 
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class PortNotFoundException extends IOException {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 5749863077414751560L;

	public PortNotFoundException(final Throwable cause) {
		super(cause);
	}

}
