package de.uniluebeck.itm.wsn.drivers.core.operation;




/**
 * Listener for observing operation events.
 * 
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 *
 * @param <T> return type of the operation
 */
public interface OperationListener<T> {

	/**
	 * Method is called before an operation state changes.
	 *
	 * @param event the event indicating the old and new state
	 */
	void beforeStateChanged(StateChangedEvent<T> event);
	
	/**
	 * Method is called after an operation state occurred.
	 * 
	 * @param event the event indicating the old and new state
	 */
	void afterStateChanged(StateChangedEvent<T> event);

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
	 * Method is called after an exception occurred.
	 *
	 * @param throwable The exception caught
	 */
	void onFailure(Throwable throwable);

	/**
	 * Method is called on a progress change.
	 *
	 * @param fraction Progress amount done.
	 */
	void onProgressChange(float fraction);
}
