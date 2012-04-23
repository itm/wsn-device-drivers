package de.uniluebeck.itm.wsn.drivers.core.serialport;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.util.concurrent.Monitor;
import de.uniluebeck.itm.wsn.drivers.core.AbstractConnection;
import de.uniluebeck.itm.wsn.drivers.core.exception.PortNotFoundException;
import de.uniluebeck.itm.wsn.drivers.core.util.JarUtil;
import de.uniluebeck.itm.wsn.drivers.core.util.SysOutUtil;
import gnu.io.*;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TooManyListenersException;

import static com.google.common.base.Preconditions.checkState;


/**
 * A simple serial port connection implementation for general purpose use of the serial port.
 *
 * @author Malte Legenhausen
 */
public abstract class AbstractSerialPortConnection extends AbstractConnection
		implements SerialPortConnection, SerialPortEventListener {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSerialPortConnection.class);

	private static final int[] DEFAULT_CHANNELS = new int[]{
			11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26
	};

	/**
	 * The default baud rate.
	 */
	public static final int DEFAULT_NORMAL_BAUD_RATE = 115200;

	/**
	 * The default program baud rate.
	 */
	public static final int DEFAULT_PROGRAM_BAUD_RATE = 38400;

	/**
	 * The maximum timeout for a connection try.
	 */
	private static final int MAX_CONNECTION_TIMEOUT = 1000;

	/**
	 * The baud rate that is used for normal operation.
	 */
	private int normalBaudRate = DEFAULT_NORMAL_BAUD_RATE;

	/**
	 * the baud rate that is used for programming the device.
	 */
	private int programBaudRate = DEFAULT_PROGRAM_BAUD_RATE;

	/**
	 * Serial port stop bits.
	 */
	private int stopBits = SerialPort.STOPBITS_1;

	/**
	 * Serial port data bits.
	 */
	private int dataBits = SerialPort.DATABITS_8;

	/**
	 * Parity bit that is used for normal operations.
	 */
	private int normalParityBit = SerialPort.PARITY_NONE;

	/**
	 * Parity bit that is used for programing.
	 */
	private int programParityBit = SerialPort.PARITY_NONE;

	/**
	 * The serial port instance.
	 */
	private SerialPort serialPort;

	static {
		LOG.trace("Loading rxtxSerial from jar file");
		JarUtil.loadLibrary("rxtxSerial");

		if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX) {
			File lockDir = new File("/var/lock");
			if (!lockDir.exists() || !lockDir.isDirectory()) {
				LOG.warn("No /var/lock directory found. Needed for RXTX Library. Try mkdir /var/lock.");
			}
			if (!lockDir.canRead() || !lockDir.canWrite()) {
				LOG.warn("/var/lock directory is not read and writable. Try chmod 777 /var/lock.");
			}
		}
	}

	@Override
	public SerialPort getSerialPort() {
		return serialPort;
	}

	@Override
	public void connect(final String port) throws IOException {
		super.connect(port);
		checkState(serialPort == null, "Serial port is already set. Disconnect first before retry.");

		try {
			connectSerialPort(port);
			setConnected();
		} catch (final NoSuchElementException e) {
			LOG.warn("Port {} not found.", port);
			throw new PortNotFoundException(e);
		} catch (final PortInUseException e) {
			LOG.error("Port {} already in use.", port);
			throw new IOException(e);
		} catch (final ClassCastException e) {
			LOG.error("Port {} is not a serial port.", port);
			throw new IOException(e);
		}
	}

	protected void connectSerialPort(final String port) throws PortInUseException, IOException {
		SysOutUtil.mute();
		Enumeration<?> identifiers;
		try {
			identifiers = CommPortIdentifier.getPortIdentifiers();
		} finally {
			SysOutUtil.restore();
		}

		Iterator<?> iterator = Iterators.forEnumeration(identifiers);
		CommPortIdentifier commPortIdentifier = (CommPortIdentifier) Iterators.find(iterator, new Predicate<Object>() {
			@Override
			public boolean apply(final Object input) {
				final CommPortIdentifier commPortIdentifier = (CommPortIdentifier) input;
				return commPortIdentifier.getName().equals(port);
			}
		}
		);

		serialPort = (SerialPort) commPortIdentifier.open(getClass().getName(), MAX_CONNECTION_TIMEOUT);
		serialPort.notifyOnDataAvailable(true);
		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			LOG.error("Listener already added.", e);
		}

		setUri(port);
		setOutputStream(serialPort.getOutputStream());
		setInputStream(serialPort.getInputStream());
	}

	@Override
	public void setSerialPortMode(final SerialPortMode mode) {
		int baudRate = normalBaudRate;
		int parityBit = normalParityBit;
		if (SerialPortMode.PROGRAM.equals(mode)) {
			baudRate = programBaudRate;
			parityBit = programParityBit;
		}

		try {
			serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parityBit);
		} catch (final UnsupportedCommOperationException e) {
			LOG.warn("Problem while setting serial port params.", e);
		}

		serialPort.setDTR(false);
		serialPort.setRTS(false);
		LOG.debug("COM-Port parameters set to baud rate: " + serialPort.getBaudRate());
	}

	@Override
	public void close() throws IOException {
		super.close();

		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	@Override
	public void serialEvent(final SerialPortEvent event) {
		switch (event.getEventType()) {
			case SerialPortEvent.DATA_AVAILABLE:
				signalDataAvailable();
				break;
			default:
				LOG.debug("Serial event (other than data available): " + event);
				break;
		}
	}

	@Override
	public int[] getChannels() {
		return DEFAULT_CHANNELS;
	}

	public int getNormalBaudRate() {
		return normalBaudRate;
	}

	public void setNormalBaudRate(final int normalBaudRate) {
		this.normalBaudRate = normalBaudRate;
	}

	public int getProgramBaudRate() {
		return programBaudRate;
	}

	public void setProgramBaudRate(final int programBaudRate) {
		this.programBaudRate = programBaudRate;
	}

	public int getStopBits() {
		return stopBits;
	}

	public void setStopBits(final int stopBits) {
		this.stopBits = stopBits;
	}

	public int getDataBits() {
		return dataBits;
	}

	public void setDataBits(final int dataBits) {
		this.dataBits = dataBits;
	}

	public int getNormalParityBit() {
		return normalParityBit;
	}

	public void setNormalParityBit(final int normalParityBit) {
		this.normalParityBit = normalParityBit;
	}

	public int getProgramParityBit() {
		return programParityBit;
	}

	public void setProgramParityBit(final int programParityBit) {
		this.programParityBit = programParityBit;
	}
}
