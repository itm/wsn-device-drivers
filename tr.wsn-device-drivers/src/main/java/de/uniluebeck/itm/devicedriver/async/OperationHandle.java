package de.uniluebeck.itm.devicedriver.async;

import de.uniluebeck.itm.devicedriver.State;

/**
 * Handle that allows the caneling of the operation.
 * 
 * @author Malte Legenhausen
 * 
 * @param <T> The type of the operation result.
 */
public interface OperationHandle<T> {
	
	/**
	 * Blocks the current operation until the result is received.
	 * 
	 * @return The operation result.
	 */
	T get();
	
	/**
	 * Cancel the operation.
	 */
	void cancel();
	
	/**
	 * Returns the current state of the operation.
	 * 
	 * @return The <code>State</code> of the operation.
	 */
	State getState();
}
