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
	private static final Logger LOG = LoggerFactory.getLogger(AbstractConnection.class);
	
	private final List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();
	
	private boolean connected = false;
	
	private InputStream inputStream;
	
	private OutputStream outputStream;
	
	private String uri;
	
	protected void setConnected(final boolean connected) {
		this.connected = connected;
		fireConnectionChange(connected);
	}
	
	protected void fireConnectionChange(final boolean connected) {
		LOG.debug(connected ? "Connected" : "Disconnected");
		final ConnectionEvent event = new ConnectionEvent(this, uri, connected);
		for (final ConnectionListener listener : listeners.toArray(new ConnectionListener[listeners.size()])) {
			listener.onConnectionChange(event);
		}
	}
	
	protected void setInputStream(final InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	protected void setOutputStream(final OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	protected void setUri(final String uri) {
		this.uri = uri;
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
	public void addListener(final ConnectionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(final ConnectionListener listener) {
		listeners.remove(listener);
	}
}
