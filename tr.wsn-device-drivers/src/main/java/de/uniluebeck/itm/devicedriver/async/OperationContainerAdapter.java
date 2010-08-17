package de.uniluebeck.itm.devicedriver.async;

import de.uniluebeck.itm.devicedriver.async.OperationContainer.State;

public class OperationContainerAdapter<T> implements OperationContainerListener<T> {

	@Override
	public void onStateChanged(OperationContainer<T> container, State oldState, State newState) {
	}

	@Override
	public void onTimeout(OperationContainer<T> container, long timeout) {
	}

}
