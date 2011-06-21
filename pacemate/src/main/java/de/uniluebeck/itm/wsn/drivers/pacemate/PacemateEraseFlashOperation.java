package de.uniluebeck.itm.wsn.drivers.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class PacemateEraseFlashOperation extends AbstractOperation<Void> implements EraseFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateEraseFlashOperation.class);
	
	private static final int START_ADDRESS = 3;
	
	private static final int END_ADDRESS = 14;
	
	private final PacemateSerialPortConnection connection;
	
	private final EnterProgramModeOperation enterProgramModeOperation;
	
	private final LeaveProgramModeOperation leaveProgramModeOperation;
	
	@Inject
	public PacemateEraseFlashOperation(PacemateSerialPortConnection connection,
			EnterProgramModeOperation enterProgramModeOperation,
			LeaveProgramModeOperation leaveProgramModeOperation) {
		this.connection = connection;
		this.enterProgramModeOperation = enterProgramModeOperation;
		this.leaveProgramModeOperation = leaveProgramModeOperation;
	}
	
	private void eraseFlash(final ProgressManager progressManager) throws Exception {
		connection.clearStreamData();
		connection.autobaud();

		connection.waitForBootLoader();

		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			return;
		}
		
		connection.configureFlash(START_ADDRESS, END_ADDRESS);
		progressManager.worked(0.25f);
		connection.eraseFlash(START_ADDRESS, END_ADDRESS);
		progressManager.done();
	}
	
	@Override
	public Void execute(final ProgressManager progressManager) throws Exception {
		log.debug("Erasing whole flash...");
		executeSubOperation(enterProgramModeOperation, progressManager.createSub(0.25f));
		try {
			eraseFlash(progressManager.createSub(0.5f));
		} finally {
			executeSubOperation(leaveProgramModeOperation, progressManager.createSub(0.25f));
		}
		log.debug("Flash completly erased");
		return null;
	}
}
