package de.uniluebeck.itm.devicedriver.serialport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection.SerialPortMode;

public class SerialPortLeaveProgramModeOperation extends AbstractOperation<Void> implements LeaveProgramModeOperation {

	private static final Logger log = LoggerFactory.getLogger(SerialPortLeaveProgramModeOperation.class);
	
	private final SerialPortConnection connection;
	
	public SerialPortLeaveProgramModeOperation(SerialPortConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		log.debug("Leaving programming mode...");
		connection.flush();
		monitor.onProgressChange(0.5f);
		connection.setSerialPortMode(SerialPortMode.NORMAL);
		monitor.onProgressChange(1.0f);
		log.debug("Programming mode left");
		return null;
	}

}
