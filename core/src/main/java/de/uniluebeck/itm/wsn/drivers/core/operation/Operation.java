package de.uniluebeck.itm.wsn.drivers.core.operation;

import java.util.concurrent.Callable;

import com.google.common.util.concurrent.TimeLimiter;

import de.uniluebeck.itm.wsn.drivers.core.State;
import de.uniluebeck.itm.wsn.drivers.core.async.AsyncCallback;


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
	 * @param progressManager The progress manager for this operation.
	 * @return The result of the <code>Operation</code> execution.
	 * @throws Exception Any exception that can occur during an operation execution.
	 */
	T execute(ProgressManager progressManager) throws Exception;
	
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
	 * Set the execution time limit for this operation.
	 * 
	 * @param timeout The limit in milliseconds.
	 */
	void setTimeout(long timeout);
	
	/**
	 * Returns the timeout for this operation.
	 * 
	 * @return The timeout of the operation.
	 */
	long getTimeout();
	
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
	
	void setTimeLimiter(TimeLimiter timeLimiter);
}
