package de.uniluebeck.itm.devicedriver.serialport;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.ConnectionEvent;
import de.uniluebeck.itm.devicedriver.ConnectionListener;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePlainText;
import de.uniluebeck.itm.devicedriver.ObserverableDevice;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;
import de.uniluebeck.itm.devicedriver.exception.TimeoutException;
import de.uniluebeck.itm.devicedriver.operation.RunningOperationsMonitor;
import de.uniluebeck.itm.devicedriver.util.StringUtils;
import de.uniluebeck.itm.devicedriver.util.TimeDiff;


/**
 * Abstract device that use a <code>SerialPort</code> for the connection with the device.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractSerialPortDevice extends ObserverableDevice implements ConnectionListener, SerialPortEventListener {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(AbstractSerialPortDevice.class);
	
	/**
	 * Data buffer for <code>MessagePlainText</code> objects.
	 */
	private byte[] plainText = new byte[2048];
	
	/**
	 * Current length of the received plainText.
	 */
	private int plainTextLength = 0;
	
	/**
	 * Data buffer for <code>MessagePacket</code> objects.
	 */
	private byte[] packet = new byte[2048];

	/** 
	 * Current packetLength of the received packet.
	 */
	private int packetLength = 0;

	/**
	 * 
	 */
	private boolean foundDLE = false;

	/**
	 * Flag that will be set when a packet was received. 
	 */
	private boolean foundPacket = false;
	
	/**
	 * Synchronisation object for data connection.
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
	public AbstractSerialPortDevice(SerialPortConnection connection) {
		this.connection = connection;
		this.connection.addListener(this);
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
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:
			synchronized (dataAvailableMonitor) {
				dataAvailableMonitor.notifyAll();
			}
			
			if (monitor.isRunning()) {
				receivePacket(connection.getInputStream());
			}
			break;
		default:
			log.debug("Serial event (other than data available): " + event);
			break;
		}
	}
	
	@Override
	public void onConnectionChange(ConnectionEvent event) {
		if (event.isConnected()) {
			try {
				this.connection.getSerialPort().addEventListener(this);
			} catch (TooManyListenersException e) {
				log.error("Can not register serial port listener", e);
			}
		}
	}
	
	/**
	 * Wait at most timeoutMillis for the input stream to become available
	 * 
	 * @param timeoutMillis Milliseconds to wait until timeout, 0 for no timeout
	 * @return The number of characters available
	 * @throws IOException
	 */
	public int waitDataAvailable(int timeoutMillis) throws TimeoutException, IOException {
		InputStream inputStream = connection.getInputStream();
		TimeDiff timeDiff = new TimeDiff();
		int available = 0;

		while (inputStream != null && (available = inputStream.available()) == 0) {
			if (timeoutMillis > 0 && timeDiff.ms() >= timeoutMillis) {
				log.warn("Timeout waiting for data (waited: " + timeDiff.ms() + ", timeoutMs:" + timeoutMillis + ")");
				throw new TimeoutException();
			}

			synchronized (dataAvailableMonitor) {
				try {
					dataAvailableMonitor.wait(50);
				} catch (InterruptedException e) {
					log.error("Interrupted: " + e, e);
				}
			}
		}
		return available;
	}
	
	private void receivePacket(InputStream inStream) {
		try {
			plainTextLength = 0;
			while (inStream != null && inStream.available() > 0) {
				final byte input = (byte) (0xff & inStream.read());
				
				// MessagePacket processing
				processMessagePacketInput(input);
				
				// PlainTextMessage processing
				processMessagePlainTextInput(input);
			}
			// Send all data left in the buffer.
			sendMessagePlainText();
		} catch (IOException error) {
			log.error("Error on rx (Retry in 1s): " + error, error);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.warn(e.getMessage());
			}
		}
	}
	
	private void processMessagePacketInput(byte input) {
		// Check if DLE was found
		if (foundDLE) {
			foundDLE = false;

			if (input == MessagePacket.STX && !foundPacket) {
				//log.debug("iSenseDeviceImpl: STX received in DLE mode");
				foundPacket = true;
			} else if (input == MessagePacket.ETX && foundPacket) {
				//log.debug("ETX received in DLE mode");

				// Parse message and notify listeners
				MessagePacket p = MessagePacket.parse(packet, 0, packetLength);
				// p.setIsenseDevice(this);
				//log.debug("Packet found: " + p);
				fireMessagePacketEvent(new MessageEvent<MessagePacket>(this, p));

				// Reset packet information
				clearPacket();
			} else if (input == MessagePacket.DLE && foundPacket) {
				// Stuffed DLE found
				//log.debug("Stuffed DLE received in DLE mode");
				ensureBufferSize();
				packet[packetLength++] = MessagePacket.DLE;
			} else {
				log.error("Incomplete packet received: " + StringUtils.toHexString(this.packet, 0, packetLength));
				clearPacket();
			}
		} else {
			if (input == MessagePacket.DLE) {
				log.debug("Plain DLE received");
				foundDLE = true;
			} else if (foundPacket) {
				ensureBufferSize();
				packet[packetLength++] = input;
			}
		}
	}
	
	private void processMessagePlainTextInput(byte c) {
		if ((plainTextLength + 1) < plainText.length) {
			plainText[plainTextLength++] = c;
		} else {
			sendMessagePlainText();
		}
	}
	
	private void sendMessagePlainText() {
		// Copy them into a buffer with correct length
		byte[] buffer = new byte[plainTextLength];
		System.arraycopy(plainText, 0, buffer, 0, plainTextLength);

		// Notify listeners
		MessagePlainText p = new MessagePlainText(buffer);
		fireMessagePlainTextEvent(new MessageEvent<MessagePlainText>(this, p));

		// Reset packet information
		plainTextLength = 0;
	}
	
	/**
	 * 
	 */
	private void ensureBufferSize() {
		if (packetLength + 1 >= this.packet.length) {
			byte tmp[] = new byte[packetLength + 100];
			System.arraycopy(this.packet, 0, tmp, 0, packetLength);
			this.packet = tmp;
		}
	}
	
	private void clearPacket() {
		packetLength = 0;
		foundDLE = false;
		foundPacket = false;
	}
}
