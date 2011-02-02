package de.uniluebeck.itm.devicedriver.pacemate;

import gnu.io.SerialPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.ResetOperation;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;

public class PacemateResetOperation extends AbstractOperation<Void> implements ResetOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateResetOperation.class);
	
	private final SerialPortConnection connection;
	
	public PacemateResetOperation(final SerialPortConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Void execute(final Monitor monitor) throws Exception {
		log.debug("Resetting device device...");
		SerialPort serialPort = connection.getSerialPort();
		serialPort.setDTR(true);
		monitor.onProgressChange(0.5f);
		Thread.sleep(200);
		serialPort.setDTR(false);
		monitor.onProgressChange(1.0f);
		log.debug("Device resetted");
		return null;
	}

}
