package de.uniluebeck.itm.devicedriver.event;

import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.operation.Operation;

public class StateChangedEvent<T> extends OperationEvent<T> {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = -4839584275095334359L;

	private final State oldState;
	
	private final State newState;
	
	/**
	 * Constructor.
	 * 
	 * @param operation
	 * @param oldState
	 * @param newState
	 */
	public StateChangedEvent(Operation<T> operation, State oldState, State newState) {
		this(operation, operation, oldState, newState);
	}
	
	/**
	 * Use this constructor if the source of the event is not the operation.
	 * 
	 * @param source
	 * @param operation
	 * @param oldState
	 * @param newState
	 */
	public StateChangedEvent(Object source, Operation<T> operation, State oldState, State newState) {
		super(source, operation);
		
		this.oldState = oldState;
		this.newState = newState;
	}
	
	public State getOldState() {
		return oldState;
	}
	
	public State getNewState() {
		return newState;
	}
}
