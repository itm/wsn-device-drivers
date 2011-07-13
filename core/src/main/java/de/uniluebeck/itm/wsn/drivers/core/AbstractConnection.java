package de.uniluebeck.itm.wsn.drivers.core;

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.google.common.io.Closeables;


/**
 * Class that implement the common functionality of a connection class.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractConnection implements Connection {
	
	/**
	 * List for connectionListeners that want to be notified on connection state changes.
	 */
	private final List<ConnectionListener> connectionListeners = newArrayList();

	private final List<DataAvailableListener> dataAvailableListeners = newArrayList();
	
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
		fireConnectionChange(new ConnectionEvent(this, uri, connected));
	}
	
	/**
	 * Fire a connection change event to all registered connectionListeners.
	 * 
	 * @param event The event you want to fire.
	 */
	protected void fireConnectionChange(ConnectionEvent event) {
		for (final ConnectionListener listener : connectionListeners.toArray(new ConnectionListener[0])) {
			listener.onConnectionChange(event);
		}
	}
	
	protected void fireDataAvailableListeners(ConnectionEvent event) {
		for (DataAvailableListener dataAvailableListener : dataAvailableListeners) {
			dataAvailableListener.dataAvailable(event);
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
	public void addListener(final ConnectionListener listener) {
		connectionListeners.add(listener);
	}

	@Override
	public void removeListener(final ConnectionListener listener) {
		connectionListeners.remove(listener);
	}

	@Override
	public void addListener(final DataAvailableListener listener) {
		dataAvailableListeners.add(listener);
	}

	@Override
	public void removeListener(final DataAvailableListener listener) {
		dataAvailableListeners.remove(listener);
	}
	
	@Override
	public void close() throws IOException {
		setConnected(false);
		Closeables.close(inputStream, true);
		inputStream = null;
		Closeables.close(outputStream, true);
		outputStream = null;
	}
}
