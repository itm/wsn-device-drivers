package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractWriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

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
	public Void execute(final ProgressManager monitor) throws Exception {
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
