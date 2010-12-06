package de.uniluebeck.itm.devicedriver.event;

import de.uniluebeck.itm.devicedriver.operation.Operation;

public class RemovedEvent<T> extends OperationEvent<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6596578633061914325L;

	public RemovedEvent(Object source, Operation<T> operation) {
		super(source, operation);
	}
}
