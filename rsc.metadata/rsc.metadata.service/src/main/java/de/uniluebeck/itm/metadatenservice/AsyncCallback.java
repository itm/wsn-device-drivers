package de.uniluebeck.itm.metadatenservice;

import de.uniluebeck.itm.rsc.drivers.core.Monitor;

/**
 * Callback method for async operations.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type of the value that is returned when the operation is done.
 */
public interface AsyncCallback<T> extends Monitor {
	
	/**
	 * Method is called on successfully method execution.
	 * 
	 * @param result The result of the operation.
	 */
	void onSuccess(T result);
	
	/**
	 * Method is called when the operation was canceled.
	 */
	void onCancel();
	
	/**
	 * Method is called when an exception occured.
	 * 
	 * @param throwable The catched exception.
	 */
	void onFailure(Throwable throwable);
}
