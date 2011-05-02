package de.uniluebeck.itm.wsn.drivers.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class PacemateEraseFlashOperation extends AbstractOperation<Void> implements EraseFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateEraseFlashOperation.class);
	
	private static final int START_ADDRESS = 3;
	
	private static final int END_ADDRESS = 14;
	
	private final PacemateDevice device;
	
	public PacemateEraseFlashOperation(PacemateDevice device) {
		this.device = device;
	}
	
	private void eraseFlash(final ProgressManager progressManager) throws Exception {
		device.clearStreamData();
		device.autobaud();

		// Wait for a connection
		while (!isCanceled() && !device.waitForConnection()) {
			log.info("Still waiting for a connection...");
		}

		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			return;
		}
		
		device.configureFlash(START_ADDRESS, END_ADDRESS);
		progressManager.worked(0.25f);
		device.eraseFlash(START_ADDRESS, END_ADDRESS);
		progressManager.done();
	}
	
	@Override
	public Void execute(final ProgressManager progressManager) throws Exception {
		log.debug("Erasing whole flash...");
		executeSubOperation(device.createEnterProgramModeOperation(), progressManager.createSub(0.25f));
		try {
			eraseFlash(progressManager.createSub(0.5f));
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), progressManager.createSub(0.25f));
		}
		log.debug("Flash completly erased");
		return null;
	}
}
