package de.uniluebeck.itm.wsn.drivers.core.async;

import java.util.List;

import com.google.inject.ImplementedBy;

import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;

/**
 * Interface that defines a queue thats manages the execution of <code>Operation</code>s.
 * 
 * @author Malte Legenhausen
 */
@ImplementedBy(ExecutorServiceOperationQueue.class)
public interface OperationQueue {

	/**
	 * Add a operation to the queue and execute it.
	 * 
	 * @param <T> The return type of the operation.
	 * @param operation The operation that has to be added to the queue.
	 * @param timeout The timeout of the operation execution.
	 * @param callback The callback that is called during the operation execution.
	 * @return A <code>OperationFuture</code> for controlling the operation.
	 */
	<T> OperationFuture<T> addOperation(Operation<T> operation, long timeout, AsyncCallback<T> callback);
	
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
	void addListener(OperationQueueListener listener);
	
	/**
	 * Remove a listener from the queue.
	 * 
	 * @param listener The listener that has to be removed.
	 */
	void removeListener(OperationQueueListener listener);
}
