package de.uniluebeck.itm.tcp.server.exceptions;

public class DuplacateIdException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6043614111522858243L;
	
	public DuplacateIdException(String id){
		super("Duplacate ID: "+id);
	}

}
