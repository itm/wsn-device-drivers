package de.uniluebeck.itm.wsn.drivers.core.io;


/**
 * Interface for classes that are able to be locked.
 * 
 * @author Malte Legenhausen
 */
public interface Lockable {

	void setLocked(boolean locked);
	
	boolean isLocked();
}
