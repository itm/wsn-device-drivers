package de.uniluebeck.itm.wsn.drivers.core.operation;

import de.uniluebeck.itm.wsn.drivers.core.event.StateChangedEvent;



/**
 * Listener for observing operation events.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> Return type of the operation.
 */
public interface OperationListener<T> {
	
	void beforeStateChanged(StateChangedEvent<T> event);
	
	/**
	 * Method is called when an operation state change occurs.
	 * 
	 * @param event The <code>StateChangedEvent</code> that occurs.
	 */
	void afterStateChanged(StateChangedEvent<T> event);
}
