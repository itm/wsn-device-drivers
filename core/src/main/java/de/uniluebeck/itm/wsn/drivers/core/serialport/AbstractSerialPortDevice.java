package de.uniluebeck.itm.wsn.drivers.core.serialport;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.tr.util.TimeDiff;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionEvent;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionListener;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import de.uniluebeck.itm.wsn.drivers.core.io.LockedInputStreamManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;


/**
 * Abstract device that use a <code>SerialPort</code> for the connection with the device.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractSerialPortDevice implements Device<SerialPortConnection>, ConnectionListener, SerialPortEventListener {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSerialPortDevice.class);
	
	/**
	 * The timeout that will be waited for available data.
	 */
	private static final int DATA_AVAILABLE_TIMEOUT = 50;
	
	/**
	 * Synchronization object for data connection.
	 */
	private final Object dataAvailableMonitor = new Object();
	
	/**
	 * <code>SerialPortConnection</code> for this device.
	 */
	private final SerialPortConnection connection;
	
	/**
	 * Manager for managing a <code>LockedInputStream</code>.
	 */
	private final LockedInputStreamManager lockedInputStreamManager = new LockedInputStreamManager();
	
	/**
	 * Constructor.
	 * 
	 * @param connection The serial port connection for this device.
	 */
	public AbstractSerialPortDevice(final SerialPortConnection connection) {
		this.connection = connection;
		this.connection.addListener(this);
		this.connection.addListener(lockedInputStreamManager);
	}

	@Override
	public SerialPortConnection getConnection() {
		return connection;
	}
	
	/**
	 * Register a created operation for monitoring purposes by the device.
	 * 
	 * @param <T> Return type of the operation.
	 * @param operation The operation object that has to be monitored.
	 */
	protected <T> Operation<T> monitor(final Operation<T> operation) {
		return lockedInputStreamManager.monitor(operation);
	}
	
	@Override
	public void serialEvent(final SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:			
			synchronized (dataAvailableMonitor) {
				dataAvailableMonitor.notifyAll();
			}
			break;
		default:
			LOG.debug("Serial event (other than data available): " + event);
			break;
		}
	}
	
	@Override
	public void onConnectionChange(final ConnectionEvent event) {
		if (event.isConnected()) {
			try {
				connection.getSerialPort().addEventListener(this);
			} catch (final TooManyListenersException e) {
				LOG.error("Can not register serial port listener", e);
			}
		}
	}
	
	/**
	 * Wait at most timeoutMillis for the input stream to become available
	 * 
	 * @param timeout Milliseconds to wait until timeout, 0 for no timeout
	 * @return The number of characters available
	 * @throws TimeoutException when no data was available for the timeout duration.
	 * @throws IOException when something went wrong with the input stream.
	 */
	public int waitDataAvailable(final int timeout) throws TimeoutException, IOException {
		LOG.debug("Waiting for data...");
		
		final InputStream inputStream = connection.getInputStream();
		final TimeDiff timeDiff = new TimeDiff();
		int available = inputStream.available();

		while (available == 0) {
			if (timeout > 0 && timeDiff.ms() >= timeout) {
				LOG.warn("Timeout waiting for data (waited: " + timeDiff.ms() + ", timeoutMs:" + timeout + ")");
				throw new TimeoutException();
			}

			synchronized (dataAvailableMonitor) {
				try {
					dataAvailableMonitor.wait(DATA_AVAILABLE_TIMEOUT);
				} catch (final InterruptedException e) {
					LOG.error("Interrupted: " + e, e);
				}
			}
			available = inputStream.available();
		}
		return available;
	}
	
	@Override
	public InputStream getInputStream() {
		return lockedInputStreamManager.getInputStream();
	}
}
