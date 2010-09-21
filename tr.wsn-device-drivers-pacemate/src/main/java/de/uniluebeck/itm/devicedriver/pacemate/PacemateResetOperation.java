package de.uniluebeck.itm.devicedriver.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.SerialPort;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.SerialPortConnection;
import de.uniluebeck.itm.devicedriver.operation.AbstractResetOperation;

public class PacemateResetOperation extends AbstractResetOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateResetOperation.class);
	
	private final SerialPortConnection connection;
	
	public PacemateResetOperation(SerialPortConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		log.debug("Resetting device Pacemate style");
		
		SerialPort serialPort = connection.getSerialPort();
		serialPort.setDTR(true);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}
		serialPort.setDTR(false);
		return null;
	}

}
