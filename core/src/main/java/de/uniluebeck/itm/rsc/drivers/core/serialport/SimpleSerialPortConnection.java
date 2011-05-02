package de.uniluebeck.itm.rsc.drivers.core.serialport;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.uniluebeck.itm.rsc.drivers.core.AbstractConnection;
import de.uniluebeck.itm.rsc.drivers.core.util.JarUtil;
import de.uniluebeck.itm.rsc.drivers.core.util.SysOutUtil;


/**
 * A simple serial port connection implementation for general purpose use of the serial port.
 * 
 * @author Malte Legenhausen
 */
public class SimpleSerialPortConnection extends AbstractConnection implements SerialPortConnection {

	/**
	 * The default max retries when calling the connect mehtod.
	 */
	public static final int DEFAULT_MAX_RETRIES = 5;
	
	/**
	 * The default baudrate.
	 */
	public static final int DEFAULT_NORMAL_BAUDRATE = 115200;

	/**
	 * The default program baudrate.
	 */
	public static final int DEFAULT_PROGRAM_BAUDRATE = 38400;
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SimpleSerialPortConnection.class);
	
	/**
	 * Time that is waited between each reconnect.
	 */
	private static final int SLEEP_BETWEEN_RETRIES = 200;
	
	/**
	 * The maximum timeout for a connection try.
	 */
	private static final int MAX_CONNECTION_TIMEOUT = 1000;
	
	/**
	 * The amount of retrys before canceling the connect process.
	 */
	private int maxRetries = DEFAULT_MAX_RETRIES;
	
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
		LOG.debug("Loading rxtxSerial from jar file");
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
			LOG.error("Port already in use. Connection will be removed. ");
			if (serialPort != null) {
				serialPort.close();
			}
			throw new RuntimeException(e);
		} catch (final Exception e) {
			if (serialPort != null) {
				serialPort.close();
			}
			LOG.error("Port " + port + " does not exist. Connection will be removed. " + e, e);
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
		SerialPort sp = null;
		while (identifiers.hasMoreElements()) {
			final CommPortIdentifier cpi = (CommPortIdentifier) identifiers.nextElement();
			if (cpi.getName().equals(port)) {
				CommPort commPort = null;
				for (int i = 0; i < maxRetries; i++) {
					try {
						commPort = cpi.open(this.getClass().getName(), MAX_CONNECTION_TIMEOUT);
						break;
					} catch (final PortInUseException piue) {
						LOG.error("Port in Use Retrying to connect");
						if (i >= maxRetries - 1) {
							throw piue;
						}
						Thread.sleep(SLEEP_BETWEEN_RETRIES);
					}
				}
				if (commPort instanceof SerialPort) {
					sp = (SerialPort) commPort;// cpi.open("iShell", 1000);
				} else {
					LOG.debug("Port is no SerialPort");
				}
				break;
			}
		}
		serialPort = sp;
		serialPort.notifyOnDataAvailable(true);

		setUri(port);
		setOutputStream(serialPort.getOutputStream());
		setInputStream(serialPort.getInputStream());
	}
	
	@Override
	public void setSerialPortMode(final SerialPortMode mode) {
		int baudrate = normalBaudrate;
		int parityBit = normalParityBit;
		if (mode == SerialPortMode.PROGRAM) {
			baudrate = programBaudrate;
			parityBit = programParitiyBit;
		}

		LOG.debug("Set com port " + baudrate + " " + databits + " " + stopbits + " " + parityBit);
		try {
			serialPort.setSerialPortParams(baudrate, databits, stopbits, parityBit);
		} catch (final UnsupportedCommOperationException e) {
			LOG.warn("Problem while setting serial port params", e);
		}

		serialPort.setDTR(false);
		serialPort.setRTS(false);
		LOG.debug("Setting COM-Port parameters (new style): baudrate: " + serialPort.getBaudRate());
	}

	@Override
	public void shutdown(final boolean force) {
		try {
			if (getInputStream() != null) {
				getInputStream().close();
			}
		} catch (final IOException e) {
			LOG.error("Failed to close in-stream :" + e, e);
		}
		try {
			if (getOutputStream() != null) {
				getOutputStream().close();
			}
		} catch (final IOException e) {
			LOG.error("Failed to close out-stream :" + e, e);
		}
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
			setConnected(false);
			serialPort = null;
		}
	}

	/** 
	 * 
	 */
	@Override
	public void flush() {
		long count = 0;
		LOG.debug("Flushing serial rx buffer");
		try {
			while ((count = getInputStream().available()) > 0) {
				LOG.debug("Skipping " + count + " characters while flushing on the serial rx");
				getInputStream().skip(count);
			}
		} catch (final IOException e) {
			LOG.error("Error while serial rx flushing buffer: " + e, e);
		}
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(final int maxRetries) {
		this.maxRetries = maxRetries;
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
