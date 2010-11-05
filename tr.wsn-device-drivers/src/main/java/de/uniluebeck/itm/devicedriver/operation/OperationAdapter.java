package de.uniluebeck.itm.devicedriver.operation;

import de.uniluebeck.itm.devicedriver.State;


/**
 * Null implementation of the <code>OperationListener</code>.
 * Override the methods you want to use.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type of the <code>Operation</code>.
 */
public class OperationAdapter<T> implements OperationListener<T> {

	@Override
	public void onStateChanged(Operation<T> operation, State oldState, State newState) {
	}

	@Override
	public void onTimeout(Operation<T> operation, long timeout) {
	}
}
