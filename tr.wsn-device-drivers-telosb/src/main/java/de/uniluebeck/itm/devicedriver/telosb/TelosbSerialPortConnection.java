package de.uniluebeck.itm.devicedriver.telosb;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.AbstractConnection;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;

public class TelosbSerialPortConnection extends AbstractConnection implements SerialPortConnection {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(TelosbSerialPortConnection.class);
	
	/**
	 * Time out for opening a serial port 
	 */
	private static final int PORTOPEN_TIMEOUTMS = 1000;
	
	private static final int READ_BAUDRATE = 115200;
	
	private static final int FLASH_BAUDRATE = 9600;
	
	private static final int MAX_OPEN_PORT_RETRIES = 10;
	
	private static final int BSL_DATABITS = SerialPort.DATABITS_8;
	private static final int BSL_STOPBITS = SerialPort.STOPBITS_1;
	private static final int BSL_PARITY_EVEN = SerialPort.PARITY_EVEN;
	private static final int BSL_PARITY_NONE = SerialPort.PARITY_NONE;
	
	private SerialPort serialPort;

	@Override
	public void connect(String uri) {
		Enumeration<?> allIdentifiers = null;
		CommPortIdentifier portIdentifier = null;
		CommPort commPort = null;
		int tries = 0;
		boolean portOpened = false;
		boolean portFound = false;

		if (serialPort != null) {
			return;
		}

		allIdentifiers = CommPortIdentifier.getPortIdentifiers();
		while (allIdentifiers.hasMoreElements() && !portFound) {
			portIdentifier = (CommPortIdentifier) allIdentifiers.nextElement();
			if (portIdentifier.getName().equals(uri)) {
				portFound = true;
			}
		}

		if (!portFound) {
			log.error("Failed to connect to port '" + uri
					+ "': port does not exist.");
			throw new RuntimeException("Failed to connect to port '" + uri
					+ "': port does not exist.");
		}

		// open port
		while (tries < MAX_OPEN_PORT_RETRIES && !portOpened) {
			try {
				tries++;
				commPort = portIdentifier.open(this.getClass().getName(),
						PORTOPEN_TIMEOUTMS);
				portOpened = true;
			} catch (PortInUseException e) {
				if (tries < MAX_OPEN_PORT_RETRIES) {
					log.debug("Port '" + uri
							+ "' is already in use, retrying to connect...");
					portOpened = false;
				} else {
					log.debug("Port '" + uri + "' is already in use, failed to connect.");
					throw new RuntimeException("Port '" + uri + "' is already in use, failed to connect.");
				}
			}
		}

		// cancel if opened port is no serial port
		if (!(commPort instanceof SerialPort)) {
			log.error("Com Port '" + uri + "' is no serial port, will not connect.");
			throw new RuntimeException("Com Port '" + uri + "' is no serial port, will not connect.");
		}

		serialPort = (SerialPort) commPort;
		try {
			serialPort.setSerialPortParams(READ_BAUDRATE, BSL_DATABITS,
					BSL_STOPBITS, BSL_PARITY_NONE);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		} catch (UnsupportedCommOperationException e) {
			log.error("Failed to connect to port '" + uri + "'. " + e.getMessage(), e);
			throw new RuntimeException(e);
		}

		serialPort.setRTS(true);
		serialPort.setDTR(true);

		try {
			setInputStream(serialPort.getInputStream());
			setOutputStream(serialPort.getOutputStream());
		} catch (IOException e) {
			log.error("Unable to get I/O streams of port " + uri + ", failed to connect.", e);
			throw new RuntimeException(e);
		}
		
		serialPort.notifyOnDataAvailable(true);
		setUri(uri);
		setConnected(true);
		log.debug("Device connected to serial port " + serialPort.getName());
	}

	@Override
	public void shutdown(boolean force) {
		
		if (log.isDebugEnabled()) {
			log.debug("Shutting down device");
		}

		if (getInputStream() != null) {
			try {
				getInputStream().close();
			} catch (IOException e) {
				log.debug("Unable to close input stream: " + e);
			}
		}
		if (getOutputStream() != null) {
			try {
				getOutputStream().close();
			} catch (IOException e) {
				log.debug("Unable to close output stream: " + e);
			}
		}

		if (serialPort != null) {
			serialPort.setRTS(true);
			serialPort.setDTR(false);

			serialPort.removeEventListener();
			serialPort.close();
			serialPort = null;
		}
		setConnected(false);
	}

	@Override
	public SerialPort getSerialPort() {
		return serialPort;
	}

	@Override
	public void setSerialPortMode(SerialPortMode mode) {
		try {
			if (mode == SerialPortMode.PROGRAM) {
				changeComPort(FLASH_BAUDRATE, BSL_PARITY_EVEN);
			} else {
				changeComPort(READ_BAUDRATE, BSL_PARITY_NONE);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
	
	/**
	 * Change the baudrate and parity information for data transmissions.
	 * 
	 * @param newBaud
	 * @return true if baud rate was changed successfully, false if no ACK was received
	 * @throws IOException
	 */
	private void changeComPort(int newBaud, int newParity) throws IOException {
		// set new baudrate for serial port
		try {
			serialPort.setSerialPortParams(newBaud, serialPort.getDataBits(), serialPort.getStopBits(), newParity);
		} catch (UnsupportedCommOperationException e) {
			log.error("Error changing baudrate: " + e);
			throw new IOException("Error changing baudrate: " + e);
		}
	}

	@Override
	public void flush() {
		long count = 0;
		log.debug("Flushing serial rx buffer");
		try {
			while ((count = getInputStream().available()) > 0) {
				log.debug("Skipping " + count + " characters while flushing on the serial rx");
				getInputStream().skip(count);
			}
		} catch (IOException e) {
			log.error("Error while serial rx flushing buffer: " + e, e);
		}
	}	
}
