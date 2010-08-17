package de.uniluebeck.itm.devicedriver.async;

import java.util.List;

import de.uniluebeck.itm.devicedriver.Operation;

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
	 * @param timeout The timeout after which the operation has to be canceled.
	 * @param callback The callback interface that is called when the operation is done.
	 * @return A <code>OperationHandle</code> for controlling the operation.
	 */
	<T> OperationHandle<T> addOperation(Operation<T> operation, int timeout, AsyncCallback<T> callback);
	
	/**
	 * Returns all operations that are in the queue.
	 * 
	 * @return All operations left in the queue.
	 */
	List<OperationContainer<?>> getOperations();
}
