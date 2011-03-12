package de.uniluebeck.itm.devicedriver.operation;

import de.uniluebeck.itm.devicedriver.event.StateChangedEvent;



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
	 * @param event The <code>StateChangedEvent</code> that occurs.
	 */
	void onStateChanged(StateChangedEvent<T> event);
}
