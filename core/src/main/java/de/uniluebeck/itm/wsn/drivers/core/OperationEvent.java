package de.uniluebeck.itm.wsn.drivers.core;

import java.util.EventObject;

import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;


/**
 * An abstract exvent class that contains an <code>Operation</code>.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The type of the operation.
 */
public abstract class OperationEvent<T> extends EventObject {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 7123840474019747685L;
	
	/**
	 * The operation associated with this event.
	 */
	private final Operation<T> operation;
	
	/**
	 * Constructor.
	 * Can be used when the sender is the contained operation.
	 * 
	 * @param operation The operation associated with this event.
	 */
	public OperationEvent(final Operation<T> operation) {
		this(operation, operation);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param source The source of this event.
	 * @param operation The operation associated with this event.
	 */
	public OperationEvent(final Object source, final Operation<T> operation) {
		super(source);
		this.operation = operation;
	}
	
	/**
	 * Getter for the operation.
	 * 
	 * @return The operation associated with this event.
	 */
	public Operation<T> getOperation() {
		return operation;
	}
}
