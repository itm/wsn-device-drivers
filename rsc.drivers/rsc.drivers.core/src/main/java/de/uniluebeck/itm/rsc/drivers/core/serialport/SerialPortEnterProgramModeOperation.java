package de.uniluebeck.itm.rsc.drivers.core.serialport;

import gnu.io.SerialPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.rsc.drivers.core.exception.EnterProgramModeException;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractProgressManager;
import de.uniluebeck.itm.rsc.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.rsc.drivers.core.serialport.SerialPortConnection.SerialPortMode;


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
	public Void execute(final AbstractProgressManager progressManager) throws Exception {
		LOG.debug("Entering program mode");
		connection.setSerialPortMode(SerialPortMode.PROGRAM);
		
		final SerialPort serialPort = connection.getSerialPort();
		try {
			serialPort.setDTR(true);
			progressManager.worked(0.25f);
			Thread.sleep(SLEEP);
			serialPort.setRTS(true);
			progressManager.worked(0.25f);
			Thread.sleep(SLEEP);
			serialPort.setDTR(false);
			progressManager.worked(0.25f);
			Thread.sleep(SLEEP);
			serialPort.setRTS(false);
		} catch(final InterruptedException e) {
			LOG.error("Unable to enter program mode.", e);
			throw new EnterProgramModeException("Unable to enter program mode.");
		}
		LOG.debug("Program mode entered");
		return null;
	}

}
