package de.uniluebeck.itm.devicedriver.operation;

import de.uniluebeck.itm.devicedriver.State;


public interface OperationListener<T> {
	
	void onStateChanged(Operation<T> operation, State oldState, State newState);
}
