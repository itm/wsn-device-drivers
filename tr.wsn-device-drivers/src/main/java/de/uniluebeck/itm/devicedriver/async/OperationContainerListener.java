package de.uniluebeck.itm.devicedriver.async;

import de.uniluebeck.itm.devicedriver.async.OperationContainer.State;

public interface OperationContainerListener<T> {
	
	void onTimeout(OperationContainer<T> container, long timeout);
	
	void onStateChanged(OperationContainer<T> container, State oldState, State newState);
}
