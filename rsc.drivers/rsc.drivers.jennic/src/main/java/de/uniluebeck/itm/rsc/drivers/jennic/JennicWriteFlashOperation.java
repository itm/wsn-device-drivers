package de.uniluebeck.itm.rsc.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.rsc.drivers.core.Monitor;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractWriteFlashOperation;

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
		log.trace("Writing to flash...");
		executeSubOperation(device.createEnterProgramModeOperation(), monitor);
		try {
			device.writeFlash(getAddress(), getData());
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), monitor);
		}
		log.trace("Flash written");
		return null;
	}

	
}
