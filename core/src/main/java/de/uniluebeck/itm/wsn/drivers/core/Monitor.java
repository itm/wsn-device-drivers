package de.uniluebeck.itm.wsn.drivers.core;

/**
 * Monitor interface that is used populating the progress of a operation.
 * 
 * @author Malte Legenhausen
 */
public interface Monitor {
	
	/**
	 * Method is called on a progress change.
	 * 
	 * @param fraction Progress amount done.
	 */
	void onProgressChange(float fraction);
}
