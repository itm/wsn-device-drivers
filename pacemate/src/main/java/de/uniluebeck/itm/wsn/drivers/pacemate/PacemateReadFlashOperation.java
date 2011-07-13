package de.uniluebeck.itm.wsn.drivers.pacemate;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class PacemateReadFlashOperation extends AbstractReadFlashOperation {
	
	private final PacemateHelper helper;
	
	private final EnterProgramModeOperation enterProgramModeOperation;
	
	private final LeaveProgramModeOperation leaveProgramModeOperation;
	
	@Inject
	public PacemateReadFlashOperation(PacemateHelper helper,
			EnterProgramModeOperation enterProgramModeOperation,
			LeaveProgramModeOperation leaveProgramModeOperation) {
		this.helper = helper;
		this.enterProgramModeOperation = enterProgramModeOperation;
		this.leaveProgramModeOperation = leaveProgramModeOperation;
	}
	
	private byte[] readFlash(final ProgressManager progressManager) throws Exception {
		helper.clearStreamData();
		helper.autobaud();

		helper.waitForBootLoader();

		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			return null;
		}
		
		// Send flash program request
		helper.sendBootLoaderMessage(Messages.flashReadRequestMessage(getAddress(), getLength()));
		progressManager.worked(0.5f);
		
		// Read flash program response
		byte[] response = helper.receiveBootLoaderReplyReadData();
		progressManager.done();
		
		// Return data
		return response;
	}
	
	@Override
	public byte[] execute(final ProgressManager progressManager) throws Exception {
		executeSubOperation(enterProgramModeOperation, progressManager.createSub(0.25f));
		byte[] result = null;
		try {
			result = readFlash(progressManager.createSub(0.5f));
		} finally {
			executeSubOperation(leaveProgramModeOperation, progressManager.createSub(0.25f));
		}
		return result;
	}
}
