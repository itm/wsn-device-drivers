package de.uniluebeck.itm.devicedriver;

/**
 * Monitor interface that is used populating the progress of a operation.
 * 
 * @author Malte Legenhausen
 */
public interface Monitor {
	
	/**
	 * Method is called on a progress change.
	 * 
	 * @param Progress amount done.
	 */
	void progress(float fraction);
}
