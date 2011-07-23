package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.uniluebeck.itm.wsn.drivers.core.exception.FlashEraseFailedException;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class JennicEraseFlashOperation implements EraseFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicEraseFlashOperation.class);
	
	private final JennicHelper helper;
	
	private final Provider<EnterProgramModeOperation> enterProgramModeProvider;
	
	private final Provider<LeaveProgramModeOperation> leaveProgramModeProvider;
	
	@Inject
	public JennicEraseFlashOperation(JennicHelper helper,
			Provider<EnterProgramModeOperation> enterProgramModeProvider, 
			Provider<LeaveProgramModeOperation> leaveprogramModeProvider) {
		this.helper = helper;
		this.enterProgramModeProvider = enterProgramModeProvider;
		this.leaveProgramModeProvider = leaveprogramModeProvider;
	}
	
	private void eraseFlash(final ProgressManager progressManager, OperationContext context) throws Exception {
		helper.sendBootLoaderMessage(Messages.statusRegisterWriteMessage((byte) 0x00));
		progressManager.worked(0.25f);
		byte[] response = helper.receiveBootLoaderReply(Messages.WRITE_SR_RESPONSE);

		if (response[1] != 0x0) {
			log.error(String.format("Failed to write status register."));
			throw new FlashEraseFailedException();
		}
		
		if (context.isCanceled()) {
			return;
		}
		
		progressManager.worked(0.25f);
		log.trace("Erasing flash");
		helper.sendBootLoaderMessage(Messages.flashEraseRequestMessage());
		response = helper.receiveBootLoaderReply(Messages.FLASH_ERASE_RESPONSE);

		if (response[1] != 0x0) {
			throw new FlashEraseFailedException("Failed to erase flash.");
		}
		progressManager.done();
	}
	
	@Override
	public Void run(final ProgressManager progressManager, OperationContext context) throws Exception {
		context.execute(enterProgramModeProvider.get(), progressManager, 0.25f);
		try {
			eraseFlash(progressManager.createSub(0.5f), context);
		} finally {
			context.execute(leaveProgramModeProvider.get(), progressManager, 0.25f);
		}
		return null;
	}

}
