package de.uniluebeck.itm.wsn.drivers.core.operation;


/**
 * Callback method for async operations.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type of the value that is returned when the operation is done.
 */
public interface OperationCallback<T> extends ProgressCallback {
	
	/**
	 * Method is called when the operation will now be executed.
	 */
	void onExecute();
	
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
