package de.uniluebeck.itm.devicedriver.async;

import de.uniluebeck.itm.devicedriver.operation.Operation;
import de.uniluebeck.itm.devicedriver.operation.OperationEvent;

public class AddedEvent<T> extends OperationEvent<T> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3039830138996966262L;

	public AddedEvent(Object source, Operation<T> operation) {
		super(source, operation);
	}
}
