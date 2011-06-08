package de.uniluebeck.itm.wsn.drivers.core.async;

import de.uniluebeck.itm.wsn.drivers.core.event.AddedEvent;
import de.uniluebeck.itm.wsn.drivers.core.event.RemovedEvent;
import de.uniluebeck.itm.wsn.drivers.core.event.StateChangedEvent;


/**
 * Default implementation for the OperationQueueListener.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type of the operation.
 */
public class OperationQueueAdapter<T> implements OperationQueueListener<T> {

	@Override
	public void onStateChanged(StateChangedEvent<T> event) {
		
	}

	@Override
	public void onAdded(AddedEvent<T> event) {
		
	}

	@Override
	public void onRemoved(RemovedEvent<T> event) {
		
	}

}
