package de.uniluebeck.itm.devicedriver.operation;

import java.util.concurrent.Callable;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;


/**
 * A device operation.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type of the operation.
 */
public interface Operation<T> extends Callable<T> {

	/**
	 * Set method for the <code>AsyncCallback</code>.
	 * 
	 * @param callback The callback that is called when the method has finished or raised an exception.
	 */
	void setAsyncCallback(AsyncCallback<T> callback);
	
	/**
	 * Method that is called when the operation has to be executed.
	 * 
	 * @param monitor The monitor for this operation.
	 * @return The result of the <code>Operation<code> execution.
	 */
	T execute(Monitor monitor) throws Exception;
	
	/**
	 * Cancel the operation.
	 */
	void cancel();
	
	/**
	 * Returns if the operation will be canceled.
	 * 
	 * @return true if the operation will be canceled else false.
	 */
	boolean isCanceled();
	
	/**
	 * Returns the state of the operation.
	 * 
	 * @return The operation state.
	 */
	State getState();
	
	/**
	 * Returns the timeout for this operation.
	 * 
	 * @return The timeout of the operation.
	 */
	Long getTimeout();
	
	/**
	 * Start the timer that will change the operation state to <code>State.TIMEDOUT</code>.
	 * 
	 * @param timeout The timeout in milliseconds.
	 */
	void scheduleTimeout(long timeout);
	
	/**
	 * Adds an <code>OperationListener</code> to the listener list.
	 * 
	 * @param listener The <code>OperationListener</code> that has to be added.
	 */
	void addListener(OperationListener<T> listener);
	
	/**
	 * Removes the given <code>OperationListener</code> from the listener list.
	 * 
	 * @param listener The <code>OperationListener</code> that has to be removed.
	 */
	void removeOperationListener(OperationListener<T> listener);
}
