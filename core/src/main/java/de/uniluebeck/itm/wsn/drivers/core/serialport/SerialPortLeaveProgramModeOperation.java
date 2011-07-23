package de.uniluebeck.itm.wsn.drivers.core.serialport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection.SerialPortMode;


/**
 * <code>LeaveProgramModeOperation</code> implementation for <code>SerialPort</code> attached devices.
 * 
 * @author Malte Legenhausen
 */
public class SerialPortLeaveProgramModeOperation implements LeaveProgramModeOperation {
	
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
	@Inject
	public SerialPortLeaveProgramModeOperation(final SerialPortConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
		LOG.trace("Leaving programming mode...");
		connection.clear();
		progressManager.worked(ProgressManager.WORKED_HALF);
		connection.setSerialPortMode(SerialPortMode.NORMAL);
		LOG.trace("Programming mode left");
		return null;
	}

}
