package de.uniluebeck.itm.devicedriver.async;

import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.operation.Operation;

public interface OperationQueueListener {
	void onStateChanged(Operation<?> operation, State oldState, State newState);
	
	void onTimeout(Operation<?> operation, long timeout);
	
	void onAdded(Operation<?> operation);
	
	void onRemoved(Operation<?> operation);
}
