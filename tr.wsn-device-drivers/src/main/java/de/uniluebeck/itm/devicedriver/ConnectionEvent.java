package de.uniluebeck.itm.devicedriver;

import java.util.EventObject;

public class ConnectionEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3281221922983359032L;
	
	private final boolean connected;
	
	private final String uri;
	
	public ConnectionEvent(Object source, String uri, boolean connected) {
		super(source);
		this.uri = uri;
		this.connected = connected;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public String getUri() {
		return uri;
	}
}
