package de.uniluebeck.itm.wsn.drivers.core.serialport;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.io.ByteStreams;

import de.uniluebeck.itm.wsn.drivers.core.AbstractConnection;
import de.uniluebeck.itm.wsn.drivers.core.util.JarUtil;
import de.uniluebeck.itm.wsn.drivers.core.util.SysOutUtil;


/**
 * A simple serial port connection implementation for general purpose use of the serial port.
 * 
 * @author Malte Legenhausen
 */
public class SimpleSerialPortConnection extends AbstractConnection implements SerialPortConnection {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SimpleSerialPortConnection.class);
	
	/**
	 * The default baudrate.
	 */
	public static final int DEFAULT_NORMAL_BAUDRATE = 115200;

	/**
	 * The default program baudrate.
	 */
	public static final int DEFAULT_PROGRAM_BAUDRATE = 38400;
	
	/**
	 * The maximum timeout for a connection try.
	 */
	private static final int MAX_CONNECTION_TIMEOUT = 1000;
	
	/**
	 * The baudrate that is used for normal operation.
	 */
	private int normalBaudrate = DEFAULT_NORMAL_BAUDRATE;
	
	/**
	 * the baudrate that is used for programming the device.
	 */
	private int programBaudrate = DEFAULT_PROGRAM_BAUDRATE;
	
	/**
	 * Serial port stop bits.
	 */
	private int stopbits = SerialPort.STOPBITS_1;

	/**
	 * Serial port databits.
	 */
	private int databits = SerialPort.DATABITS_8;

	/**
	 * Paritiy bit that is used for normal operations.
	 */
	private int normalParityBit = SerialPort.PARITY_NONE;
	
	/**
	 * Paritiy bit that is used for programing.
	 */
	private int programParitiyBit = SerialPort.PARITY_NONE;

	/**
	 * The serial port instance.
	 */
	private SerialPort serialPort;
	
	static {
		LOG.trace("Loading rxtxSerial from jar file");
		JarUtil.loadLibrary("rxtxSerial");
	}
	
	@Override
	public SerialPort getSerialPort() {
		return serialPort;
	}
	
	@Override
	public void connect(final String port) {
		Preconditions.checkNotNull(port, "The given port can not be null.");
		Preconditions.checkState(serialPort == null, "Serial port is already set. Disconnect first before retry.");

		try {
			connectSerialPort(port);
			setConnected(true);
		} catch (final PortInUseException e) {
			LOG.error("Port already in use. Connection will be removed.", e);
			throw new RuntimeException(e);
		} catch (final ClassCastException e) {
			LOG.error("Port " + port + " is not a serial port.", e);
			throw new RuntimeException(e);
		} catch (final Exception e) {
			LOG.error("Port " + port + " does not exist. Connection will be removed.", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Connect to the serial port for this device.
	 * 
	 * @param port The port string.
	 * @throws Exception 
	 */
	protected void connectSerialPort(final String port) throws Exception {
		SysOutUtil.mute();
		final Enumeration<?> identifiers = CommPortIdentifier.getPortIdentifiers();
		SysOutUtil.restore();
		final Iterator<?> iterator = Iterators.forEnumeration(identifiers);
		final CommPortIdentifier commPortIdentifier = (CommPortIdentifier) Iterators.find(iterator, new Predicate<Object>() {
			@Override
			public boolean apply(final Object input) {
				final CommPortIdentifier commPortIdentifier = (CommPortIdentifier) input;
				return commPortIdentifier.getName().equals(port);
			}
		});
		serialPort = (SerialPort) commPortIdentifier.open(getClass().getName(), MAX_CONNECTION_TIMEOUT);
		serialPort.notifyOnDataAvailable(true);

		setUri(port);
		setOutputStream(serialPort.getOutputStream());
		setInputStream(serialPort.getInputStream());
	}
	
	@Override
	public void setSerialPortMode(final SerialPortMode mode) {
		int baudrate = normalBaudrate;
		int parityBit = normalParityBit;
		if (SerialPortMode.PROGRAM.equals(mode)) {
			baudrate = programBaudrate;
			parityBit = programParitiyBit;
		}

		try {
			serialPort.setSerialPortParams(baudrate, databits, stopbits, parityBit);
		} catch (final UnsupportedCommOperationException e) {
			LOG.warn("Problem while setting serial port params.", e);
		}

		serialPort.setDTR(false);
		serialPort.setRTS(false);
		LOG.debug("COM-Port parameters set to baudrate: " + serialPort.getBaudRate());
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
			serialPort = null;	
		}
		setConnected(false);
	}

	/** 
	 * 
	 */
	@Override
	public void flush() {
		LOG.trace("Flushing serial rx buffer");
		final InputStream in = getInputStream();
		try {
			ByteStreams.skipFully(in, in.available());
		} catch (IOException e) {
			LOG.error("Error while serial rx flushing buffer: " + e, e);
		}
	}

	public int getNormalBaudrate() {
		return normalBaudrate;
	}

	public void setNormalBaudrate(final int normalBaudrate) {
		this.normalBaudrate = normalBaudrate;
	}

	public int getProgramBaudrate() {
		return programBaudrate;
	}

	public void setProgramBaudrate(final int programBaudrate) {
		this.programBaudrate = programBaudrate;
	}

	public int getStopbits() {
		return stopbits;
	}

	public void setStopbits(final int stopbits) {
		this.stopbits = stopbits;
	}

	public int getDatabits() {
		return databits;
	}

	public void setDatabits(final int databits) {
		this.databits = databits;
	}
	
	public int getNormalParityBit() {
		return normalParityBit;
	}

	public void setNormalParityBit(final int normalParityBit) {
		this.normalParityBit = normalParityBit;
	}

	public int getProgramParitiyBit() {
		return programParitiyBit;
	}

	public void setProgramParitiyBit(final int programParitiyBit) {
		this.programParitiyBit = programParitiyBit;
	}
}
