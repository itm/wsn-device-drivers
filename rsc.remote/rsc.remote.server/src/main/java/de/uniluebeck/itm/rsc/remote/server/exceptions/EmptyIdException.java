package de.uniluebeck.itm.rsc.remote.server.exceptions;

/**
 * Exception for Empty Id-Entrys in the devices.xml
 * @author Andreas Maier
 *
 */
public class EmptyIdException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -37405997527842174L;

	/**
	 * Constructor .
	 */
	public EmptyIdException(){
		super("empty Id's are not allowed!");
	}

}
