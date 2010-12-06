package de.uniluebeck.itm.devicedriver.event;

import java.util.EventObject;

import de.uniluebeck.itm.devicedriver.Message;

public class MessageEvent<T extends Message> extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1999180029389695356L;
	
	private final T message;
	
	public MessageEvent(Object source, T message) {
		super(source);
		this.message = message;
	}
	
	public T getMessage() {
		return message;
	}
}
