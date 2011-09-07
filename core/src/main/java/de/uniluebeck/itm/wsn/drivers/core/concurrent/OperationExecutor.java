package de.uniluebeck.itm.wsn.drivers.core.concurrent;

import java.util.List;

import com.google.inject.ImplementedBy;

import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;

/**
 * Interface that defines a executor thats manages the execution of <code>OperationRunnable</code>s.
 * 
 * @author Malte Legenhausen
 */
@ImplementedBy(ExecutorServiceOperationExecutor.class)
public interface OperationExecutor {

	/**
	 * Submit an operation and execute it as long the timeout is not reached.
	 * The <code>OperationCallback</code> is used for receiving asynchronious events.
	 * 
	 * @param <T> The return type of the operation.
	 * @param operation The operation that has to be added to the queue.
	 * @return A <code>OperationFuture</code> for controlling the operation.
	 */
	<T> OperationFuture<T> submitOperation(Operation<T> operation);
	
	/**
	 * Returns all operations that are in the queue.
	 * 
	 * @return All operations left in the queue.
	 */
	List<Operation<?>> getOperations();
	
	/**
	 * Add a listener to the queue. 
	 * 
	 * @param listener A listener that handle queue events.
	 */
	void addListener(OperationExecutorListener listener);
	
	/**
	 * Remove a listener from the queue.
	 * 
	 * @param listener The listener that has to be removed.
	 */
	void removeListener(OperationExecutorListener listener);
}
