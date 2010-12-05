package de.uniluebeck.itm.devicedriver.operation;

import java.util.EventObject;

public class OperationEvent<T> extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7123840474019747685L;
	
	private final Operation<T> operation;
	
	public OperationEvent(Operation<T> operation) {
		this(operation, operation);
	}
	
	public OperationEvent(Object source, Operation<T> operation) {
		super(source);
		this.operation = operation;
	}
	
	public Operation<T> getOperation() {
		return operation;
	}
}
