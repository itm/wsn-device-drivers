package de.uniluebeck.itm.devicedriver.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractReadFlashOperation;

public class PacemateReadFlashOperation extends AbstractReadFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateReadFlashOperation.class);
	
	private PacemateDevice device;
	
	public PacemateReadFlashOperation(PacemateDevice device) {
		this.device = device;
	}
	
	private byte[] readFlash(Monitor monitor) throws Exception {
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
		monitor.onProgressChange(0.5f);
		
		// Read flash program response
		byte[] response = device.receiveBootLoaderReplyReadData();
		monitor.onProgressChange(1.0f);
		
		// Return data
		return response;
	}
	
	@Override
	public byte[] execute(Monitor monitor) throws Exception {
		executeSubOperation(device.createEnterProgramModeOperation(), monitor);
		byte[] result = null;
		try {
			result = readFlash(monitor);
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), monitor);
		}
		return result;
	}
}
