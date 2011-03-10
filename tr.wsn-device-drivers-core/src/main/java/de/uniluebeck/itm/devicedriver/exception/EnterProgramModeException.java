package de.uniluebeck.itm.devicedriver.exception;


/**
 * Use this exception when the program mode of a device can not be entered.
 * 
 * @author Malte Legenhausen
 */
public class EnterProgramModeException extends RuntimeException {
	
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 3384665639559615437L;

	/**
	 * Constructor.
	 * 
	 * @param message A description for the exception.
	 */
	public EnterProgramModeException(final String message) {
		super(message);
	}
}
