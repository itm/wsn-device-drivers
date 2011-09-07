package de.uniluebeck.itm.wsn.drivers.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class PacemateEraseFlashOperation implements EraseFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateEraseFlashOperation.class);
	
	private static final int START_ADDRESS = 3;
	
	private static final int END_ADDRESS = 14;
	
	private final PacemateHelper helper;
	
	private final EnterProgramModeOperation enterProgramModeOperation;
	
	private final LeaveProgramModeOperation leaveProgramModeOperation;
	
	@Inject
	public PacemateEraseFlashOperation(PacemateHelper helper,
			EnterProgramModeOperation enterProgramModeOperation,
			LeaveProgramModeOperation leaveProgramModeOperation) {
		this.helper = helper;
		this.enterProgramModeOperation = enterProgramModeOperation;
		this.leaveProgramModeOperation = leaveProgramModeOperation;
	}
	
	private void eraseFlash(ProgressManager progressManager, OperationContext context) throws Exception {
		helper.clearStreamData();
		helper.autobaud();

		helper.waitForBootLoader();

		// Return with success if the user has requested to cancel this
		// operation
		if (context.isCanceled()) {
			return;
		}
		
		helper.configureFlash(START_ADDRESS, END_ADDRESS);
		progressManager.worked(0.25f);
		helper.eraseFlash(START_ADDRESS, END_ADDRESS);
		progressManager.done();
	}
	
	@Override
	public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
		log.debug("Erasing whole flash...");
		context.run(enterProgramModeOperation, progressManager.createSub(0.25f));
		try {
			eraseFlash(progressManager.createSub(0.5f), context);
		} finally {
			context.run(leaveProgramModeOperation, progressManager.createSub(0.25f));
		}
		log.debug("Flash completly erased");
		return null;
	}
}
