package de.uniluebeck.itm.wsn.drivers.core.async;

import de.uniluebeck.itm.wsn.drivers.core.event.AddedEvent;
import de.uniluebeck.itm.wsn.drivers.core.event.RemovedEvent;
import de.uniluebeck.itm.wsn.drivers.core.event.StateChangedEvent;


/**
 * Listener for observing <code>OperationQueue</code> changes.
 * 
 * @author Malte Legenhausen
 */
public interface OperationQueueListener {
	
	void beforeStateChanged(StateChangedEvent<?> event);
	
	/**
	 * Method is called when an operation state change occurs.
	 * 
	 * @param event The <code>StateChangedEvent</code> that occurs.
	 */
	void afterStateChanged(StateChangedEvent<?> event);
	
	/**
	 * Method is called when a operation is added to the queue.
	 * 
	 * @param event The operation that is added to the queue.
	 */
	void onAdded(AddedEvent<?> event);
	
	/**
	 * Method is called when a operation is removed from the queue.
	 * 
	 * @param event The operation that is removed from the queue.
	 */
	void onRemoved(RemovedEvent<?> event);
}
