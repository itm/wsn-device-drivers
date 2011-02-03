package de.uniluebeck.itm.devicedriver.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractWriteFlashOperation;

public class JennicWriteFlashOperation extends AbstractWriteFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicWriteFlashOperation.class);
	
	private final JennicDevice device;
	
	public JennicWriteFlashOperation(JennicDevice device) {
		this.device = device;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		log.debug("Writing to flash...");
		executeSubOperation(device.createEnterProgramModeOperation(), monitor);
		try {
			device.writeFlash(address, data);
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), monitor);
		}
		log.debug("Flash written");
		return null;
	}

	
}
