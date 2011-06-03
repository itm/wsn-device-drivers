package de.uniluebeck.itm.wsn.drivers.isense;

import java.io.IOException;
import java.io.InputStream;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.tr.util.TimeDiff;
import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SimpleSerialPortConnection;


/**
 * Generic serial port connection type for isense devices.
 * 
 * @author Malte Legenhausen
 */
public class iSenseSerialPortConnection extends SimpleSerialPortConnection implements SerialPortEventListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(iSenseSerialPortConnection.class);
	
	/**
	 * The timeout that will be waited for available data.
	 */
	private static final int DATA_AVAILABLE_TIMEOUT = 50;
	
	private static final int NORMAL_BAUDRATE = 115200;

	private static final int PROGRAM_BAUDRATE = 38400;
	
	/**
	 * Synchronization object for data connection.
	 */
	private final Object dataAvailableMonitor = new Object();
	
	public iSenseSerialPortConnection() {
		setNormalBaudrate(NORMAL_BAUDRATE);
		setProgramBaudrate(PROGRAM_BAUDRATE);
	}
	
	@Override
	protected void connectSerialPort(String port) throws Exception {
		super.connectSerialPort(port);
		getSerialPort().addEventListener(this);
		setSerialPortMode(SerialPortMode.NORMAL);
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
		
		final InputStream inputStream = getInputStream();
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
}
