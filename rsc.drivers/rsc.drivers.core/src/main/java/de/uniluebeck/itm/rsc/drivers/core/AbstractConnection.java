package de.uniluebeck.itm.rsc.drivers.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class that implement the common functionality of a connection class.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractConnection implements Connection {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractConnection.class);
	
	/**
	 * List for listeners that want to be notified on connection state changes.
	 */
	private final List<ConnectionListener> listeners = new ArrayList<ConnectionListener>();
	
	/**
	 * Current connection state.
	 */
	private boolean connected = false;
	
	/**
	 * Input stream of the connection.
	 */
	private InputStream inputStream;
	
	/**
	 * Output stream of the connection.
	 */
	private OutputStream outputStream;
	
	/**
	 * The uri of the connected resource.
	 */
	private String uri;
	
	/**
	 * Setter for the connection that will fire a connection change event.
	 * 
	 * @param connected True when connected else false.
	 */
	protected void setConnected(final boolean connected) {
		this.connected = connected;
		fireConnectionChange(connected);
	}
	
	/**
	 * Fire a connection change event to all registered listeners.
	 * 
	 * @param connected True when connected else false.
	 */
	protected void fireConnectionChange(final boolean connected) {
		LOG.debug(connected ? "Connected" : "Disconnected");
		final ConnectionEvent event = new ConnectionEvent(this, uri, connected);
		for (final ConnectionListener listener : listeners.toArray(new ConnectionListener[listeners.size()])) {
			listener.onConnectionChange(event);
		}
	}
	
	/**
	 * Setter for the input stream.
	 * 
	 * @param inputStream The input stream object.
	 */
	protected void setInputStream(final InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	/**
	 * Setter for the output stream.
	 * 
	 * @param outputStream The output stream object.
	 */
	protected void setOutputStream(final OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	/**
	 * Setter for the uri.
	 * 
	 * @param uri The string representation of the resource.
	 */
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
