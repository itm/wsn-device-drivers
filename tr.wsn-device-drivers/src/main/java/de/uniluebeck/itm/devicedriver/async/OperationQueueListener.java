package de.uniluebeck.itm.devicedriver.async;

import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.operation.Operation;

/**
 * Listener for observing <code>OperationQueue</code> changes.
 * 
 * @author Malte Legenhausen
 */
public interface OperationQueueListener {
	
	/**
	 * Method is called when an operation state change occurs.
	 * 
	 * @param operation The operation that changed his state.
	 * @param oldState The <code>State</code> before the state change.
	 * @param newState The <code>State</code> after the state change.
	 */
	void onStateChanged(Operation<?> operation, State oldState, State newState);
	
	/**
	 * Method is called when a operation is added to the queue.
	 * 
	 * @param operation The operation that is added to the queue.
	 */
	void onAdded(Operation<?> operation);
	
	/**
	 * Method is called when a operation is removed from the queue.
	 * 
	 * @param operation The operation that is removed from the queue.
	 */
	void onRemoved(Operation<?> operation);
}
