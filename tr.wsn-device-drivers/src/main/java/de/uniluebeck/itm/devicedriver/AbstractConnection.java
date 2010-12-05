package de.uniluebeck.itm.devicedriver;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConnection implements Connection {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(AbstractConnection.class);
	
	private final List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();
	
	private boolean connected = false;
	
	private InputStream inputStream;
	
	private OutputStream outputStream;
	
	protected void setConnected(boolean connected) {
		this.connected = connected;
		fireConnectionChange(connected);
	}
	
	protected void fireConnectionChange(boolean connected) {
		log.debug(connected ? "Connected" : "Disconnected");
		final ConnectionEvent event = new ConnectionEvent(this, connected);
		for (final ConnectionListener listener : listeners.toArray(new ConnectionListener[listeners.size()])) {
			listener.onConnectionChange(event);
		}
	}
	
	protected void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	protected void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	@Override
	public InputStream getInputStream() {
		return inputStream;
	}
	
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}
	
	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public void addListener(ConnectionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(ConnectionListener listener) {
		listeners.remove(listener);
	}
}
