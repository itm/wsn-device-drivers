package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.uniluebeck.itm.wsn.drivers.core.exception.FlashEraseFailedException;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class JennicEraseFlashOperation extends AbstractOperation<Void> implements EraseFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicEraseFlashOperation.class);
	
	private final JennicSerialPortConnection connection;
	
	private final Provider<EnterProgramModeOperation> enterProgramModeProvider;
	
	private final Provider<LeaveProgramModeOperation> leaveProgramModeProvider;
	
	@Inject
	public JennicEraseFlashOperation(JennicSerialPortConnection connection, 
			Provider<EnterProgramModeOperation> enterProgramModeProvider, 
			Provider<LeaveProgramModeOperation> leaveprogramModeProvider) {
		this.connection = connection;
		this.enterProgramModeProvider = enterProgramModeProvider;
		this.leaveProgramModeProvider = leaveprogramModeProvider;
	}
	
	private void eraseFlash(final ProgressManager progressManager) throws Exception {
		connection.sendBootLoaderMessage(Messages.statusRegisterWriteMessage((byte) 0x00));
		progressManager.worked(0.25f);
		byte[] response = connection.receiveBootLoaderReply(Messages.WRITE_SR_RESPONSE);

		if (response[1] != 0x0) {
			log.error(String.format("Failed to write status register."));
			throw new FlashEraseFailedException();
		}
		
		if (isCanceled()) {
			return;
		}
		
		progressManager.worked(0.25f);
		log.trace("Erasing flash");
		connection.sendBootLoaderMessage(Messages.flashEraseRequestMessage());
		response = connection.receiveBootLoaderReply(Messages.FLASH_ERASE_RESPONSE);

		if (response[1] != 0x0) {
			throw new FlashEraseFailedException("Failed to erase flash.");
		}
		progressManager.done();
	}
	
	@Override
	public Void execute(final ProgressManager progressManager) throws Exception {
		executeSubOperation(enterProgramModeProvider.get(), progressManager.createSub(0.25f));
		try {
			eraseFlash(progressManager.createSub(0.5f));
		} finally {
			executeSubOperation(leaveProgramModeProvider.get(), progressManager.createSub(0.25f));
		}
		return null;
	}

}
