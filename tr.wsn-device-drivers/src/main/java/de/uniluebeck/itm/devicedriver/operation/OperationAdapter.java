package de.uniluebeck.itm.devicedriver.operation;

import de.uniluebeck.itm.devicedriver.State;


public class OperationAdapter<T> implements OperationListener<T> {

	@Override
	public void onStateChanged(Operation<T> operation, State oldState, State newState) {
	}

	@Override
	public void onTimeout(Operation<T> operation, long timeout) {
	}

}
