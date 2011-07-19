package de.uniluebeck.itm.wsn.drivers.core.concurrent;

import de.uniluebeck.itm.wsn.drivers.core.operation.StateChangedEvent;


/**
 * Default implementation for the OperationExecutorListener.
 * 
 * @author Malte Legenhausen
 */
public class OperationExecutorAdapter implements OperationExecutorListener {

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
