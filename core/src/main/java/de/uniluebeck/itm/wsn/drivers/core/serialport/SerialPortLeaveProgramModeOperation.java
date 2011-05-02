package de.uniluebeck.itm.wsn.drivers.core.serialport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection.SerialPortMode;


/**
 * <code>LeaveProgramModeOperation</code> implementation for <code>SerialPort</code> attached devices.
 * 
 * @author Malte Legenhausen
 */
public class SerialPortLeaveProgramModeOperation extends AbstractOperation<Void> implements LeaveProgramModeOperation {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SerialPortLeaveProgramModeOperation.class);
	
	/**
	 * Connection used for setting the serial port mode back to normal.
	 */
	private final SerialPortConnection connection;
	
	/**
	 * Constructor.
	 * 
	 * @param connection Connection used for leaving the programming mode.
	 */
	public SerialPortLeaveProgramModeOperation(final SerialPortConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Void execute(final ProgressManager progressManager) throws Exception {
		LOG.debug("Leaving programming mode...");
		connection.flush();
		progressManager.worked(0.5f);
		connection.setSerialPortMode(SerialPortMode.NORMAL);
		LOG.debug("Programming mode left");
		return null;
	}

}
