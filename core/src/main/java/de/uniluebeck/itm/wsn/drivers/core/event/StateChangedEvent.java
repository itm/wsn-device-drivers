package de.uniluebeck.itm.wsn.drivers.core.event;

import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;
import de.uniluebeck.itm.wsn.drivers.core.operation.State;


/**
 * Event that is used when the state of an <code>Operation</code> has changed.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The type of the operation.
 */
public class StateChangedEvent<T> extends OperationEvent<T> {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = -4839584275095334359L;

	/**
	 * The old state before the change.
	 */
	private final State oldState;
	
	/**
	 * The new state after the change.
	 */
	private final State newState;
	
	/**
	 * Constructor.
	 * 
	 * @param operation The operation that has changed.
	 * @param oldState The old state of the operation.
	 * @param newState The new state of the operation.
	 */
	public StateChangedEvent(final Operation<T> operation, final State oldState, final State newState) {
		this(operation, operation, oldState, newState);
	}
	
	/**
	 * Use this constructor if the source of the event is not the operation.
	 * 
	 * @param source The source of the event.
	 * @param operation The operation that has changed.
	 * @param oldState The old state of the operation.
	 * @param newState The new state of the operation.
	 */
	public StateChangedEvent(Object source, Operation<T> operation, State oldState, State newState) {
		super(source, operation);
		
		this.oldState = oldState;
		this.newState = newState;
	}
	
	/**
	 * Getter for the old state of the operation.
	 * 
	 * @return The old state.
	 */
	public State getOldState() {
		return oldState;
	}
	
	/**
	 * Getter for the new state of the operation.
	 * 
	 * @return The new state.
	 */
	public State getNewState() {
		return newState;
	}
}
