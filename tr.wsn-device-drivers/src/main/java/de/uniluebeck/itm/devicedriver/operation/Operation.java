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
	 * Initialisation method that is called before the method is commited to the scheduler.
	 * 
	 * @param timeout The timeout of the <code>Operation</code>.
	 * @param callback The callback that is called when the method has finished or raised an exception.
	 */
	void init(long timeout, AsyncCallback<T> callback);
	
	/**
	 * Method that is called when the operation has to be executed.
	 * 
	 * @param monitor The monitor for this operation.
	 * @return The result of the <code>Operation<code> execution.
	 */
	T execute(Monitor monitor);
	
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
	long getTimeout();
	
	/**
	 * Adds an <code>OperationListener</code> to the listener list.
	 * 
	 * @param listener The <code>OperationListener</code> that has to be added.
	 */
	void addOperationListener(OperationListener<T> listener);
	
	/**
	 * Removes the given <code>OperationListener</code> from the listener list.
	 * 
	 * @param listener The <code>OperationListener</code> that has to be removed.
	 */
	void removeOperationListener(OperationListener<T> listener);
}
