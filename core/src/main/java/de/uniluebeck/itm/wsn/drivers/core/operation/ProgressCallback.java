package de.uniluebeck.itm.wsn.drivers.core.operation;

/**
 * ProgressCallback interface that is used populating the progress of a operation.
 * 
 * @author Malte Legenhausen
 */
public interface ProgressCallback {
	
	/**
	 * Method is called on a progress change.
	 * 
	 * @param fraction Progress amount done.
	 */
	void onProgressChange(float fraction);
}
