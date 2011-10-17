package de.uniluebeck.itm.wsn.drivers.core.serialport;

import gnu.io.SerialPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.wsn.drivers.core.exception.EnterProgramModeException;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection.SerialPortMode;

/**
 * Interceptor that allows the usage of the Program annotation.
 * 
 * @author Malte Legenhausen
 */
public class SerialPortProgramInterceptor extends AbstractProgramInterceptor {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SerialPortProgramInterceptor.class);
	
	/**
	 * Sleep time between setting DTR and RTS.
	 */
	private static final int SLEEP = 200;
	
	public SerialPortProgramInterceptor(SerialPortConnection connection) {
		super(connection);
	}
	
	public void enterProgramMode(SerialPortConnection connection) throws Exception {
		LOG.trace("Entering program mode...");
		connection.setSerialPortMode(SerialPortMode.PROGRAM);
		
		final SerialPort serialPort = connection.getSerialPort();
		try {
			serialPort.setDTR(true);
			Thread.sleep(SLEEP);
			serialPort.setRTS(true);
			Thread.sleep(SLEEP);
			serialPort.setDTR(false);
			Thread.sleep(SLEEP);
			serialPort.setRTS(false);
		} catch (final InterruptedException e) {
			LOG.error("Unable to enter program mode.", e);
			throw new EnterProgramModeException("Unable to enter program mode.");
		}
		connection.clear();
		LOG.trace("Program mode entered");
	}
	
	public void leaveProgramMode(SerialPortConnection connection) throws Exception {
		LOG.trace("Leaving program mode...");
		connection.clear();
		connection.setSerialPortMode(SerialPortMode.NORMAL);
		LOG.trace("Program mode left");
	}
}
