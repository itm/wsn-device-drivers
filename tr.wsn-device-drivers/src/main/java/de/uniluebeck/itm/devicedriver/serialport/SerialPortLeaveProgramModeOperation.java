package de.uniluebeck.itm.devicedriver.serialport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection.SerialPortMode;


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
	public Void execute(final Monitor monitor) throws Exception {
		LOG.debug("Leaving programming mode...");
		connection.flush();
		monitor.onProgressChange(0.5f);
		connection.setSerialPortMode(SerialPortMode.NORMAL);
		monitor.onProgressChange(1.0f);
		LOG.debug("Programming mode left");
		return null;
	}

}
