package de.uniluebeck.itm.wsn.drivers.core;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Class that implement the common functionality of a connection class.
 * 
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public abstract class AbstractConnection implements Connection {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractConnection.class);
	
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
	private InputStream rxtxInputStream;
	
	/**
	 * Output stream of the connection.
	 */
	private OutputStream rxtxOutputStream;
	
	/**
	 * The uri of the connected resource.
	 */
	private String uri;
	
	/**
	 * Data available lock.
	 */
	private final Lock dataAvailableLock = new ReentrantLock();

	/**
	 * Condition that indicates when data is available.
	 */
	private final Condition isDataAvailable = dataAvailableLock.newCondition();

	private String lastPort = null;

	@Override
	public void connect(String port) throws IOException {
		checkNotNull(port, "Port argument for connecting to device is null");
		lastPort = port;
		if (isConnected()) {
			throw new IOException("Already connected.");
		}
		if (isClosed()) {
			throw new IOException("Connection is closed.");
		}
	}
	
	@Override
	public int waitDataAvailable(final int timeoutMillis) throws TimeoutException, IOException {

		if (rxtxInputStream.available() > 0) {
			return rxtxInputStream.available();
		}

		dataAvailableLock.lock();
		try {

			if (!isDataAvailable.await(timeoutMillis, TimeUnit.MILLISECONDS)) {
				throw new TimeoutException();
			}

			return rxtxInputStream.available();

		} catch (final InterruptedException e) {

			LOG.error("Interrupted: " + e, e);
			throw new RuntimeException(e);

		} finally {
			dataAvailableLock.unlock();
		}
	}

	protected void signalDataAvailable() {

		dataAvailableLock.lock();

		try {
			isDataAvailable.signal();
		} finally {
			dataAvailableLock.unlock();
		}

		listeners.fire().onDataAvailable(new ConnectionEvent(this, uri, connected));
	}
	
	/**
	 * Setter for the connection that will fire a connection change event.
	 */
	protected void setConnected() {
		connected = true;
	}
	
	/**
	 * Setter for the input stream.
	 * 
	 * @param inputStream The input stream object.
	 */
	protected void setInputStream(final InputStream inputStream) {
		this.rxtxInputStream = inputStream;
	}
	
	/**
	 * Setter for the output stream.
	 * 
	 * @param outputStream The output stream object.
	 */
	protected void setOutputStream(final OutputStream outputStream) {
		this.rxtxOutputStream = outputStream;
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
		return rxtxInputStream;
	}
	
	@Override
	public OutputStream getOutputStream() {
		return rxtxOutputStream;
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
			Closeables.close(rxtxInputStream, true);
			Closeables.close(rxtxOutputStream, true);
			closed = true;
		}
	}
	
	@Override
	public void clear() throws IOException {
		LOG.trace("Cleaning input stream.");
		ByteStreams.skipFully(rxtxInputStream, rxtxInputStream.available());
	}
}
