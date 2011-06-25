package de.uniluebeck.itm.wsn.drivers.core.async;

import de.uniluebeck.itm.wsn.drivers.core.event.AddedEvent;
import de.uniluebeck.itm.wsn.drivers.core.event.RemovedEvent;
import de.uniluebeck.itm.wsn.drivers.core.event.StateChangedEvent;


/**
 * Default implementation for the OperationQueueListener.
 * 
 * @author Malte Legenhausen
 */
public class OperationQueueAdapter implements OperationQueueListener {

	@Override
	public void beforeStateChanged(StateChangedEvent<?> event) {
		
	}

	@Override
	public void afterStateChanged(StateChangedEvent<?> event) {
		
	}

	@Override
	public void onAdded(AddedEvent<?> event) {
		
	}

	@Override
	public void onRemoved(RemovedEvent<?> event) {
		
	}
}
