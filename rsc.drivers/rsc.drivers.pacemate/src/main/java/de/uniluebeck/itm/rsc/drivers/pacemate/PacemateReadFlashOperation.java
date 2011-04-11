package de.uniluebeck.itm.rsc.drivers.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractProgressManager;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractReadFlashOperation;

public class PacemateReadFlashOperation extends AbstractReadFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateReadFlashOperation.class);
	
	private PacemateDevice device;
	
	public PacemateReadFlashOperation(PacemateDevice device) {
		this.device = device;
	}
	
	private byte[] readFlash(final AbstractProgressManager progressManager) throws Exception {
		device.clearStreamData();
		device.autobaud();

		// Wait for a connection
		while (!isCanceled() && !device.waitForConnection()) {
			log.info("Still waiting for a connection");
		}
		
		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			return null;
		}
		
		// Send flash program request
		device.sendBootLoaderMessage(Messages.flashReadRequestMessage(getAddress(), getLength()));
		progressManager.worked(0.5f);
		
		// Read flash program response
		byte[] response = device.receiveBootLoaderReplyReadData();
		progressManager.done();
		
		// Return data
		return response;
	}
	
	@Override
	public byte[] execute(final AbstractProgressManager progressManager) throws Exception {
		executeSubOperation(device.createEnterProgramModeOperation(), progressManager.createSub(0.25f));
		byte[] result = null;
		try {
			result = readFlash(progressManager.createSub(0.5f));
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), progressManager.createSub(0.25f));
		}
		return result;
	}
}
