package de.uniluebeck.itm.rsc.drivers.core.serialport;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.rsc.drivers.core.ConnectionEvent;
import de.uniluebeck.itm.rsc.drivers.core.ConnectionListener;
import de.uniluebeck.itm.rsc.drivers.core.ObserverableDevice;
import de.uniluebeck.itm.rsc.drivers.core.exception.TimeoutException;
import de.uniluebeck.itm.rsc.drivers.core.operation.Operation;
import de.uniluebeck.itm.rsc.drivers.core.operation.RunningOperationsMonitor;
import de.uniluebeck.itm.tr.util.TimeDiff;


/**
 * Abstract device that use a <code>SerialPort</code> for the connection with the device.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractSerialPortDevice extends ObserverableDevice<SerialPortConnection> implements ConnectionListener, SerialPortEventListener {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSerialPortDevice.class);
	
	/**
	 * The timeout that will be waited for available data.
	 */
	private static final int DATA_AVAILABLE_TIMEOUT = 50;
	
	/**
	 * The timeout that will be waited when receiving a message packet.
	 */
	private static final int RECEIVE_PACKET_TIMEOUT = 1000;
	
	/**
	 * Byte mask that is used after receiving a byte from the input stream.
	 */
	private static final int RECEIVE_MASK = 0xFF;
	
	/**
	 * List for all handlers that process byte income from the device.
	 */
	private final List<ByteReceiver> receivers = new ArrayList<ByteReceiver>();
	
	/**
	 * Synchronization object for data connection.
	 */
	private final Object dataAvailableMonitor = new Object();
	
	/**
	 * <code>SerialPortConnection</code> for this device.
	 */
	private final SerialPortConnection connection;
	
	/**
	 * Monitor for observe operations that are in <code>State.RUNNING</code>.
	 */
	private final RunningOperationsMonitor monitor = new RunningOperationsMonitor();
	
	/**
	 * Constructor.
	 * 
	 * @param connection The serial port connection for this device.
	 */
	public AbstractSerialPortDevice(final SerialPortConnection connection) {
		this.connection = connection;
		this.connection.addListener(this);
		
		addByteReceiver(new MessagePacketReceiver());
		addByteReceiver(new MessagePlainTextReceiver());
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
	protected <T> void monitorState(final Operation<T> operation) {
		monitor.monitorState(operation);
	}
	
	@Override
	public void serialEvent(final SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:
			synchronized (dataAvailableMonitor) {
				dataAvailableMonitor.notifyAll();
			}
			
			if (!monitor.isRunning()) {
				receive(connection.getInputStream());
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
				this.connection.getSerialPort().addEventListener(this);
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
	
	/**
	 * Receive data from the input stream and redirect it to the <code>ByteReceiver</code>s.
	 * 
	 * @param inputStream 
	 */
	private void receive(final InputStream inputStream) {
		LOG.debug("Receiving Packet");
		try {
			beforeReceive();
			while (inputStream != null && inputStream.available() > 0) {
				final byte input = (byte) (RECEIVE_MASK & inputStream.read());
				onReceive(input);
			}
			afterReceive();
		} catch (final IOException error) {
			LOG.error("Error on rx (Retry in 1s): " + error, error);
			try {
				Thread.sleep(RECEIVE_PACKET_TIMEOUT);
			} catch (final InterruptedException e) {
				LOG.warn(e.getMessage());
			}
		}
	}
	
	/**
	 * Call the beforeReceive method from all <code>ByteReceiver</code>s.
	 */
	private void beforeReceive() {
		for (final ByteReceiver receiver : receivers.toArray(new ByteReceiver[receivers.size()])) {
			receiver.beforeReceive();
		}
	}
	
	/**
	 * Call the onReceive method from all <code>ByteReceiver</code>s.
	 * 
	 * @param input The received data.
	 */
	private void onReceive(final byte input) {
		for (final ByteReceiver receiver : receivers.toArray(new ByteReceiver[receivers.size()])) {
			receiver.onReceive(input);
		}
	}
	
	/**
	 * Call the afterReceive method from all <code>ByteReceiver</code>s.
	 */
	private void afterReceive() {
		for (final ByteReceiver receiver : receivers.toArray(new ByteReceiver[receivers.size()])) {
			receiver.afterReceive();
		}
	}
	
	/**
	 * Add a <code>ByteReceiver</code> to the device.
	 * 
	 * @param receiver The <code>ByteReceiver</code> implementation.
	 */
	public void addByteReceiver(final ByteReceiver receiver) {
		receiver.setDevice(this);
		receivers.add(receiver);
	}
	
	/**
	 * Removes a <code>ByteReceiver</code> from the device.
	 * 
	 * @param receiver The <code>ByteReceiver</code> that has to be removed.
	 */
	public void removeByteReceiver(final ByteReceiver receiver) {
		if (receivers.contains(receiver)) {
			receivers.add(receiver);
		}
	}
}
