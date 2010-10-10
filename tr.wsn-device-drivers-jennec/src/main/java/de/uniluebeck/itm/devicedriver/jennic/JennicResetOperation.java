package de.uniluebeck.itm.devicedriver.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.SerialPort;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractResetOperation;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;

public class JennicResetOperation extends AbstractResetOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicResetOperation.class);
	
	private final SerialPort serialPort;
	
	public JennicResetOperation(SerialPortConnection connection) {
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
