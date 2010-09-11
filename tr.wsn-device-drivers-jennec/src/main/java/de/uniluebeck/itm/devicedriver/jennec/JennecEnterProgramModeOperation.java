package de.uniluebeck.itm.devicedriver.jennec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.SerialPort;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.SerialPortConnection;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;

public class JennecEnterProgramModeOperation extends AbstractOperation<Boolean> implements EnterProgramModeOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennecEnterProgramModeOperation.class);
	
	private final SerialPort serialPort;
	
	public JennecEnterProgramModeOperation(SerialPortConnection connection) {
		this.serialPort = connection.getSerialPort();
	}
	
	@Override
	public Boolean execute(Monitor monitor) throws Exception {
		log.debug("Entering program mode");
		try {
			serialPort.setDTR(true);
			monitor.onProgressChange(0.25f);
			Thread.sleep(200);
			serialPort.setRTS(true);
			monitor.onProgressChange(0.5f);
			Thread.sleep(200);
			serialPort.setDTR(false);
			monitor.onProgressChange(0.75f);
			Thread.sleep(200);
			serialPort.setRTS(false);
			monitor.onProgressChange(1.0f);
		} catch (Exception e) {
			log.error("Unable to enter program mode.");
			return false;
		}
		log.debug("Program mode entered");
		return true;
	}

}
