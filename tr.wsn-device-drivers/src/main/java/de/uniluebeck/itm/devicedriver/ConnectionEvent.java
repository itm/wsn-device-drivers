package de.uniluebeck.itm.devicedriver;

import java.util.EventObject;

public class ConnectionEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3281221922983359032L;
	
	private final boolean connected;
	
	public ConnectionEvent(Object source, boolean connected) {
		super(source);
		this.connected = connected;
	}
	
	public boolean isConnected() {
		return connected;
	}
}
