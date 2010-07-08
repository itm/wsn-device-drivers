package de.uniluebeck.itm.devicedriver.async;

/**
 * Callback method for async operations.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type of the value that is returned when the operation is done.
 */
public interface AsyncCallback<T> {
	
	/**
	 * Method is called on successfully method execution.
	 * 
	 * @param result The result of the operation.
	 */
	void done(T result);
	
	/**
	 * Method is called when the operation was canceled.
	 */
	void canceled();
	
	/**
	 * Method is called when an exception occured.
	 * 
	 * @param throwable The catched exception.
	 */
	void excepted(Throwable throwable);
	
	/**
	 * Method is called when the progress has changed.
	 * 
	 * @param fraction 
	 */
	void progress(float fraction);
}
