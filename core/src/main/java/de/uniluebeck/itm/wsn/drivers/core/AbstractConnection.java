package de.uniluebeck.itm.wsn.drivers.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

import de.uniluebeck.itm.tr.util.TimeDiff;
import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;


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
	 * The timeout that will be waited for available data.
	 */
	private static final int DATA_AVAILABLE_TIMEOUT = 50;
	
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
	
	/**
	 * Data available lock.
	 */
	private final Lock dataAvailableLock = new ReentrantLock();

	/**
	 * Condition that indicates when data is available.
	 */
	private final Condition isDataAvailable = dataAvailableLock.newCondition();
	
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
	
	@Override
	public int waitDataAvailable(final int timeout) throws TimeoutException, IOException {
		LOG.trace("Waiting for data...");
		
		final TimeDiff timeDiff = new TimeDiff();
		int available = inputStream.available();

		while (available == 0) {
			if (timeout > 0 && timeDiff.ms() >= timeout) {
				LOG.warn("Timeout waiting for data (waited: " + timeDiff.ms() + ", timeoutMs:" + timeout + ")");
				throw new TimeoutException();
			}

			dataAvailableLock.lock();
			try {
				isDataAvailable.await(DATA_AVAILABLE_TIMEOUT, TimeUnit.MILLISECONDS);
			} catch (final InterruptedException e) {
				LOG.error("Interrupted: " + e, e);
			} finally {
				dataAvailableLock.unlock();
			}
			available = inputStream.available();
		}
		return available;
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
	 * 
	 * @param connected True when connected else false.
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
	
	@Override
	public void clear() throws IOException {
		LOG.trace("Cleaning input stream.");
		ByteStreams.skipFully(inputStream, inputStream.available());
	}
}
