package de.uniluebeck.itm.wsn.drivers.core.exception;


/**
 * Exception is thrown when no port is available.
 * 
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class PortNotFoundException extends RuntimeException {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 5749863077414751560L;

	public PortNotFoundException(final Throwable cause) {
		super(cause);
	}

}
