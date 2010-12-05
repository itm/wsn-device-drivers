package de.uniluebeck.itm.devicedriver.async;

import java.util.List;

import de.uniluebeck.itm.devicedriver.operation.Operation;

/**
 * Interface that defines a queue thats manages the execution of <code>Operation</code>s.
 * 
 * @author Malte Legenhausen
 */
public interface OperationQueue {

	/**
	 * Add a operation to the queue and execute it.
	 * 
	 * @param <T> The return type of the operation.
	 * @param operation The operation that has to be added to the queue.
	 * @return A <code>OperationHandle</code> for controlling the operation.
	 */
	<T> OperationHandle<T> addOperation(Operation<T> operation, long timeout, AsyncCallback<T> callback);
	
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
	
	/**
	 * Shutdown the queue and return all operations left in the queue.
	 * 
	 * @param force Use true to stop immediately else false to wait until all operations has finished.
	 * @return A list of operations that are not executed. Only returned if force equals true.
	 */
	List<Operation<?>> shutdown(boolean force);
}
