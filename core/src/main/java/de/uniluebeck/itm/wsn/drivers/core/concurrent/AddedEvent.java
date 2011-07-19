package de.uniluebeck.itm.wsn.drivers.core.concurrent;

import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationEvent;


/**
 * Event that is used when an new operation was added to the queue.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The type of operation that was added.
 */
public class AddedEvent<T> extends OperationEvent<T> {
	
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = -3039830138996966262L;

	/**
	 * Constructor.
	 * 
	 * @param source The source of the adding.
	 * @param operation The operation that was added.
	 */
	public AddedEvent(final Object source, final Operation<T> operation) {
		super(source, operation);
	}
}
