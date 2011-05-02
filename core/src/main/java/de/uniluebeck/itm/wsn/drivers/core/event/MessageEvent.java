package de.uniluebeck.itm.wsn.drivers.core.event;

import java.util.EventObject;

import de.uniluebeck.itm.wsn.drivers.core.Message;


/**
 * Event that is used when an new <code>Message</code> was received from a device.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The specific type of the message.
 */
public class MessageEvent<T extends Message> extends EventObject {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = -1999180029389695356L;
	
	/**
	 * The message object.
	 */
	private final T message;
	
	/**
	 * Constructor.
	 * 
	 * @param source The source of the event.
	 * @param message The message that was receivced.
	 */
	public MessageEvent(final Object source, final T message) {
		super(source);
		this.message = message;
	}
	
	/**
	 * Getter for the message.
	 * 
	 * @return The received message.
	 */
	public T getMessage() {
		return message;
	}
}
