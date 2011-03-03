package de.uniluebeck.itm.tcp.server.exceptions;

public class EmptyIdException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -37405997527842174L;

	public EmptyIdException(){
		super("empty Id's are not allowed!");
	}

}
