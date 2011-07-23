package de.uniluebeck.itm.wsn.drivers.core.operation;

import java.util.concurrent.Callable;

/**
 * Container interface for a <code>OperationRunnable</code>.
 * 
 * @author Malte Legenhausen
 * 
 * @param <T>
 */
public interface Operation<T> extends Callable<T> {

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
	void addListener(OperationListener<T> listener);
	
	/**
	 * Removes the given <code>OperationListener</code> from the listener list.
	 * 
	 * @param listener The <code>OperationListener</code> that has to be removed.
	 */
	void removeListener(OperationListener<T> listener);
}
