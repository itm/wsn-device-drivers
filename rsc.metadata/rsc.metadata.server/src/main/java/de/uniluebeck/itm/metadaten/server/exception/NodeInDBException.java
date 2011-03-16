package de.uniluebeck.itm.metadaten.server.exception;
/**
 * Exception thrown if node is not in the database
 * @author Toralf Babel
 *
 */
public class NodeInDBException extends Exception {
	/**
	 * Constructor
	 * @param message message to give in case exception is thrown
	 */
	public NodeInDBException(final String message){
		super(message);
	}

}
