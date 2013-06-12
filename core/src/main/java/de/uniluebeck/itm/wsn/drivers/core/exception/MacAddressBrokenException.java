package de.uniluebeck.itm.wsn.drivers.core.exception;

public class MacAddressBrokenException extends Exception {

	public MacAddressBrokenException() {
	}

	public MacAddressBrokenException(final Throwable cause) {
		super(cause);
	}

	public MacAddressBrokenException(final String message) {
		super(message);
	}

	public MacAddressBrokenException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
