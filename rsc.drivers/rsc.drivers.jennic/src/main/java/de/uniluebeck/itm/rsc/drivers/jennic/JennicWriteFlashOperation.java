package de.uniluebeck.itm.rsc.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractProgressManager;
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
	public Void execute(final AbstractProgressManager monitor) throws Exception {
		log.trace("Writing to flash...");
		executeSubOperation(device.createEnterProgramModeOperation(), monitor.createSub(0.5f));
		try {
			device.writeFlash(getAddress(), getData());
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), monitor.createSub(0.5f));
		}
		log.trace("Flash written");
		return null;
	}

	
}
