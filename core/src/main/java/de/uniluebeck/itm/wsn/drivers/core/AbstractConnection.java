package de.uniluebeck.itm.wsn.drivers.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang3.event.EventListenerSupport;

import com.google.common.io.Closeables;


/**
 * Class that implement the common functionality of a connection class.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractConnection implements Connection {
	
	/**
	 * List for connectionListeners that want to be notified when data is available.
	 */
	private final EventListenerSupport<ConnectionListener> listeners = 
			EventListenerSupport.create(ConnectionListener.class);
	
	/**
	 * Current connection state.
	 */
	private boolean connected = false;
	
	/**
	 * Current closed state.
	 */
	private boolean closed = false;
	
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
	
	@Override
	public void connect(String aUri) throws IOException {
		checkNotNull(aUri, "Null argument is not allowed.");
		if (isConnected()) {
			throw new IOException("Already connected.");
		}
		if (isClosed()) {
			throw new IOException("Connection is closed.");
		}
	}
	
	/**
	 * Setter for the connection that will fire a connection change event.
	 * 
	 * @param connected True when connected else false.
	 */
	protected void setConnected() {
		connected = true;
	}
	
	/**
	 * Fire a connection change event to all registered connectionListeners.
	 * 
	 * @param event The event you want to fire.
	 */	
	protected void fireDataAvailableListeners(ConnectionEvent event) {
		listeners.fire().onDataAvailable(event);
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
	
	public String getUri() {
		return uri;
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
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void addListener(final ConnectionListener listener) {
		listeners.addListener(listener);
	}

	@Override
	public void removeListener(final ConnectionListener listener) {
		listeners.removeListener(listener);
	}
	
	@Override
	public void close() throws IOException {
		if (!isClosed()) {
			Closeables.close(inputStream, true);
			Closeables.close(outputStream, true);
			closed = true;
		}
	}
}
