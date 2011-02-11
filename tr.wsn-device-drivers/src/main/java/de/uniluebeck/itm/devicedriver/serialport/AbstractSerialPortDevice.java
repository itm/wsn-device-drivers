package de.uniluebeck.itm.devicedriver.serialport;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.ConnectionEvent;
import de.uniluebeck.itm.devicedriver.ConnectionListener;
import de.uniluebeck.itm.devicedriver.ObserverableDevice;
import de.uniluebeck.itm.devicedriver.exception.TimeoutException;
import de.uniluebeck.itm.devicedriver.operation.RunningOperationsMonitor;
import de.uniluebeck.itm.tr.util.TimeDiff;


/**
 * Abstract device that use a <code>SerialPort</code> for the connection with the device.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractSerialPortDevice extends ObserverableDevice implements ConnectionListener, SerialPortEventListener {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSerialPortDevice.class);
	
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
	protected final SerialPortConnection connection;
	
	/**
	 * Monitor for observe operations that are in <code>State.RUNNING</code>.
	 */
	protected final RunningOperationsMonitor monitor = new RunningOperationsMonitor();
	
	/**
	 * Constructor.
	 * 
	 * @param connection The serial port connection for this device.
	 */
	public AbstractSerialPortDevice(final SerialPortConnection connection) {
		this.connection = connection;
		this.connection.addListener(this);
		
		addReceiver(new MessagePacketReceiver());
		addReceiver(new MessagePlainTextReceiver());
	}

	@Override
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * Getter for returning the internal <code>SerialPortConnection</code>.
	 * 
	 * @return
	 */
	public SerialPortConnection getSerialPortConnection() {
		return connection;
	}
	
	@Override
	public void serialEvent(final SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:
			synchronized (dataAvailableMonitor) {
				dataAvailableMonitor.notifyAll();
			}
			
			if (!monitor.isRunning()) {
				receivePacket(connection.getInputStream());
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
	 * @throws IOException
	 */
	public int waitDataAvailable(final int timeout) throws TimeoutException, IOException {
		LOG.debug("Waiting for data...");
		
		final InputStream inputStream = connection.getInputStream();
		final TimeDiff timeDiff = new TimeDiff();
		int available = 0;

		while (inputStream != null && (available = inputStream.available()) == 0) {
			if (timeout > 0 && timeDiff.ms() >= timeout) {
				LOG.warn("Timeout waiting for data (waited: " + timeDiff.ms() + ", timeoutMs:" + timeout + ")");
				throw new TimeoutException();
			}

			synchronized (dataAvailableMonitor) {
				try {
					dataAvailableMonitor.wait(50);
				} catch (final InterruptedException e) {
					LOG.error("Interrupted: " + e, e);
				}
			}
		}
		return available;
	}
	
	private void receivePacket(final InputStream inStream) {
		LOG.debug("Receiving Packet");
		try {
			beforeReceive();
			while (inStream != null && inStream.available() > 0) {
				final byte input = (byte) (0xff & inStream.read());
				onReceive(input);
			}
			afterReceive();
		} catch (final IOException error) {
			LOG.error("Error on rx (Retry in 1s): " + error, error);
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				LOG.warn(e.getMessage());
			}
		}
	}
	
	private void beforeReceive() {
		for (final ByteReceiver receiver : receivers.toArray(new ByteReceiver[receivers.size()])) {
			receiver.beforeReceive();
		}
	}
	
	private void onReceive(final byte input) {
		for (final ByteReceiver receiver : receivers.toArray(new ByteReceiver[receivers.size()])) {
			receiver.onReceive(input);
		}
	}
	
	private void afterReceive() {
		for (final ByteReceiver receiver : receivers.toArray(new ByteReceiver[receivers.size()])) {
			receiver.afterReceive();
		}
	}
	
	public void addReceiver(final ByteReceiver receiver) {
		receiver.setDevice(this);
		receivers.add(receiver);
	}
	
	public void removeReceiver(final ByteReceiver receiver) {
		if (receivers.contains(receiver)) {
			receivers.add(receiver);
		}
	}
}
