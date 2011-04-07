package de.uniluebeck.itm.rsc.drivers.isense;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.rsc.drivers.core.AbstractConnection;
import de.uniluebeck.itm.rsc.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.rsc.drivers.core.util.JarUtil;
import de.uniluebeck.itm.rsc.drivers.core.util.SysOutUtil;

public class iSenseSerialPortConnection extends AbstractConnection implements SerialPortConnection {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(iSenseSerialPortConnection.class);
	
	private static final int MAX_RETRIES = 5;
	
	private static final int NORMAL_BAUDRATE = 115200;

	private static final int PROGRAM_BAUDRATE = 38400;
	
	private final int stopbits = SerialPort.STOPBITS_1;

	private final int databits = SerialPort.DATABITS_8;

	private final int parityBit = SerialPort.PARITY_NONE;
	
	private SerialPort serialPort = null;
	
	static {
		log.debug("Loading rxtxSerial from jar file");
		SysOutUtil.mute();
		JarUtil.loadLibrary("rxtxSerial");
		SysOutUtil.restore();
	}
	
	@Override
	public SerialPort getSerialPort() {
		return serialPort;
	}
	
	@Override
	public void connect(String port) {
		if (port == null) {
			throw new NullPointerException("Port name can not be null");
		}

		if (port != null && serialPort == null) {
			try {
				setSerialPort(port);
				if (serialPort == null) {
					log.debug("connect(): serialPort==null");
				}
			} catch (PortInUseException e) {
				log.error("Port already in use. Connection will be removed. ");
				if (serialPort != null) {
					serialPort.close();
				}
				throw new RuntimeException(e);
			} catch (Exception e) {
				if (serialPort != null) {
					serialPort.close();
				}
				log.error("Port " + port + " does not exist. Connection will be removed. " + e, e);
				throw new RuntimeException(e);
			}
		}
	}
	
	public void setSerialPort(String port) throws Exception {
		SysOutUtil.mute();
		Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
		SysOutUtil.restore();
		SerialPort sp = null;
		while (e.hasMoreElements()) {
			CommPortIdentifier cpi = (CommPortIdentifier) e.nextElement();
			if (cpi.getName().equals(port)) {
				CommPort commPort = null;
				for (int i = 0; i < MAX_RETRIES; i++) {
					try {
						commPort = cpi.open(this.getClass().getName(), 1000);
						break;
					} catch (PortInUseException piue) {
						log.error("Port in Use Retrying to connect");
						if (i >= MAX_RETRIES - 1) {
							throw piue;
						}
						Thread.sleep(200);
					}
				}
				if (commPort instanceof SerialPort) {
					sp = (SerialPort) commPort;// cpi.open("iShell", 1000);
				} else {
					log.debug("Port is no SerialPort");
				}
				break;
			}
		}
		serialPort = sp;
		serialPort.notifyOnDataAvailable(true);

		setSerialPortMode(SerialPortMode.NORMAL);

		setOutputStream(new BufferedOutputStream(serialPort.getOutputStream()));
		setInputStream(new BufferedInputStream(serialPort.getInputStream()));
		setUri(port);
		setConnected(true);
	}
	
	@Override
	public void setSerialPortMode(SerialPortMode mode) {
		int baudrate = mode == SerialPortMode.PROGRAM ? PROGRAM_BAUDRATE : NORMAL_BAUDRATE;

		log.debug("Set com port " + baudrate + " " + databits + " " + stopbits + " " + parityBit);
		try {
			serialPort.setSerialPortParams(baudrate, databits, stopbits, parityBit);
		} catch (UnsupportedCommOperationException e) {
			log.warn("Problem while setting serial port params", e);
		}

		serialPort.setDTR(false);
		serialPort.setRTS(false);
		log.debug("Setting COM-Port parameters (new style): baudrate: " + serialPort.getBaudRate());
	}

	@Override
	public void shutdown(boolean force) {
		try {
			if (getInputStream() != null) {
				getInputStream().close();
			}
		} catch (IOException e) {
			log.error("Failed to close in-stream :" + e, e);
		}
		try {
			if (getOutputStream() != null) {
				getOutputStream().close();
			}
		} catch (IOException e) {
			log.error("Failed to close out-stream :" + e, e);
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
