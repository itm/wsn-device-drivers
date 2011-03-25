package de.uniluebeck.itm.rsc.remote.server.exceptions;

/**
 * Exception for duplicated Id-Entrys in the devices.xml
 * @author Andreas Maier
 *
 */
public class DuplacateIdException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6043614111522858243L;
	
	/**
	 * Constructor .
	 * @param id the duplicated Id
	 */
	public DuplacateIdException(final String id){
		super("Duplacate ID: "+id);
	}

}
