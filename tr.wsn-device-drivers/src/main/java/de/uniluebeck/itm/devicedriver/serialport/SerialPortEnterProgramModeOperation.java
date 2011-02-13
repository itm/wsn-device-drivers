package de.uniluebeck.itm.devicedriver.serialport;

import gnu.io.SerialPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.exception.EnterProgramModeException;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection.SerialPortMode;


/**
 * <code>EnterProgrammingModeOperation</code> implementation for <code>SerialPort</code> attached devices.
 * 
 * @author Malte Legenhausen
 */
public class SerialPortEnterProgramModeOperation extends AbstractOperation<Void> implements EnterProgramModeOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SerialPortEnterProgramModeOperation.class);
	
	/**
	 * Sleep time between setting DTR and RTS.
	 */
	private static final int SLEEP = 200;
	
	/**
	 * The used serial port connection.
	 */
	private final SerialPortConnection connection;
	
	/**
	 * Constructor.
	 * 
	 * @param connection The SerialPortConnection used for entering the programming mode.
	 */
	public SerialPortEnterProgramModeOperation(final SerialPortConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Void execute(final Monitor monitor) throws Exception {
		LOG.debug("Entering program mode");
		connection.setSerialPortMode(SerialPortMode.PROGRAM);
		
		final SerialPort serialPort = connection.getSerialPort();
		try {
			serialPort.setDTR(true);
			monitor.onProgressChange(0.25f);
			Thread.sleep(SLEEP);
			serialPort.setRTS(true);
			monitor.onProgressChange(0.5f);
			Thread.sleep(SLEEP);
			serialPort.setDTR(false);
			monitor.onProgressChange(0.75f);
			Thread.sleep(SLEEP);
			serialPort.setRTS(false);
			monitor.onProgressChange(1.0f);
		} catch(final InterruptedException e) {
			LOG.error("Unable to enter program mode.", e);
			throw new EnterProgramModeException("Unable to enter program mode.");
		}
		LOG.debug("Program mode entered");
		return null;
	}

}
