package de.uniluebeck.itm.devicedriver;

import java.util.EventObject;


/**
 * This event is used indicate an connection change between connected and disconnected.
 * 
 * @author Malte Legenhausen
 */
public class ConnectionEvent extends EventObject {

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 3281221922983359032L;
	
	/**
	 * Connection state if connected (true) or not (false).
	 */
	private final boolean connected;
	
	/**
	 * Connection target.
	 */
	private final String uri;
	
	/**
	 * Constructor.
	 * 
	 * @param source The event source.
	 * @param uri Connection target.
	 * @param connected The connection state.
	 */
	public ConnectionEvent(final Object source, final String uri, final boolean connected) {
		super(source);
		this.uri = uri;
		this.connected = connected;
	}
	
	/**
	 * Returns the connection state.
	 * 
	 * @return true for is connected else false.
	 */
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * Getter for the connection target.
	 * 
	 * @return The uri that represents the connection target.
	 */
	public String getUri() {
		return uri;
	}
}
