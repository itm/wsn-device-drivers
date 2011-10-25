package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.exception.FlashEraseFailedException;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.serialport.ProgrammingMode;

public class JennicEraseFlashOperation implements EraseFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicEraseFlashOperation.class);
	
	private final JennicHelper helper;
	
	@Inject
	public JennicEraseFlashOperation(JennicHelper helper) {
		this.helper = helper;
	}
	
	@Override
	@ProgrammingMode
	public Void run(final ProgressManager progressManager, OperationContext context) throws Exception {
		helper.sendBootLoaderMessage(Messages.statusRegisterWriteMessage((byte) 0x00));
		progressManager.worked(0.25f);
		byte[] response = helper.receiveBootLoaderReply(Messages.WRITE_SR_RESPONSE);

		if (response[1] != 0x0) {
			log.error(String.format("Failed to write status register."));
			throw new FlashEraseFailedException();
		}
		
		if (context.isCanceled()) {
			return null;
		}
		
		progressManager.worked(0.25f);
		log.trace("Erasing flash");
		helper.sendBootLoaderMessage(Messages.flashEraseRequestMessage());
		response = helper.receiveBootLoaderReply(Messages.FLASH_ERASE_RESPONSE);

		if (response[1] != 0x0) {
			throw new FlashEraseFailedException("Failed to erase flash.");
		}
		return null;
	}

}
