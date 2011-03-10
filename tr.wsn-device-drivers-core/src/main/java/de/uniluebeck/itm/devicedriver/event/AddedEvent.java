package de.uniluebeck.itm.devicedriver.event;

import de.uniluebeck.itm.devicedriver.operation.Operation;


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
