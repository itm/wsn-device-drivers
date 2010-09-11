package de.uniluebeck.itm.devicedriver.jennec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.SerialPort;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.SerialPortConnection;
import de.uniluebeck.itm.devicedriver.operation.AbstractResetOperation;

public class JennecResetOperation extends AbstractResetOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennecResetOperation.class);
	
	private final SerialPort serialPort;
	
	public JennecResetOperation(SerialPortConnection connection) {
		serialPort = connection.getSerialPort();
	}
	
	@Override
	public Void execute(Monitor monitor) {
		log.info("Resetting device");
		serialPort.setDTR(true);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}
		serialPort.setDTR(false);
		return null;
	}

}
