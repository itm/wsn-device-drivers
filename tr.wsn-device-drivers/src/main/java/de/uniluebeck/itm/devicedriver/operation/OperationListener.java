package de.uniluebeck.itm.devicedriver.operation;

import de.uniluebeck.itm.devicedriver.State;


/**
 * Listener for observing operation events.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> Return type of the operation.
 */
public interface OperationListener<T> {
	
	/**
	 * Method is called when an operation state change occurs.
	 * 
	 * @param operation The operation that changed his state.
	 * @param oldState The <code>State</code> before the state change.
	 * @param newState The <code>State</code> after the state change.
	 */
	void onStateChanged(Operation<T> operation, State oldState, State newState);
}
