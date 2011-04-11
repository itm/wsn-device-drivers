package de.uniluebeck.itm.rsc.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.rsc.drivers.core.exception.FlashEraseFailedException;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractProgressManager;
import de.uniluebeck.itm.rsc.drivers.core.operation.EraseFlashOperation;

public class JennicEraseFlashOperation extends AbstractOperation<Void> implements EraseFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicEraseFlashOperation.class);
	
	private final JennicDevice device;
	
	public JennicEraseFlashOperation(JennicDevice device) {
		this.device = device;
	}
	
	private void eraseFlash(final AbstractProgressManager progressManager) throws Exception {
		device.sendBootLoaderMessage(Messages.statusRegisterWriteMessage((byte) 0x00));
		progressManager.worked(0.25f);
		byte[] response = device.receiveBootLoaderReply(Messages.WRITE_SR_RESPONSE);

		if (response[1] != 0x0) {
			log.error(String.format("Failed to write status register."));
			throw new FlashEraseFailedException();
		}
		
		if (isCanceled()) {
			return;
		}
		
		progressManager.worked(0.25f);
		log.trace("Erasing flash");
		device.sendBootLoaderMessage(Messages.flashEraseRequestMessage());
		response = device.receiveBootLoaderReply(Messages.FLASH_ERASE_RESPONSE);

		if (response[1] != 0x0) {
			throw new FlashEraseFailedException("Failed to erase flash.");
		}
		progressManager.done();
	}
	
	@Override
	public Void execute(final AbstractProgressManager progressManager) throws Exception {
		executeSubOperation(device.createEnterProgramModeOperation(), progressManager.createSub(0.25f));
		try {
			eraseFlash(progressManager.createSub(0.5f));
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), progressManager.createSub(0.25f));
		}
		return null;
	}

}
