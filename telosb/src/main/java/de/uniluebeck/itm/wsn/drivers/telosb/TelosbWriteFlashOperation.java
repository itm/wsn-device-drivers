package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractWriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class TelosbWriteFlashOperation extends AbstractWriteFlashOperation {
	
	private final EnterProgramModeOperation enterProgramModeOperation;
	
	private final LeaveProgramModeOperation leaveProgramModeOperation;
	
	private final BSLTelosb bsl;
	
	@Inject
	public TelosbWriteFlashOperation(EnterProgramModeOperation enterProgramModeOperation,
			LeaveProgramModeOperation leaveProgramModeOperation,
			BSLTelosb bsl) {
		this.enterProgramModeOperation = enterProgramModeOperation;
		this.leaveProgramModeOperation = leaveProgramModeOperation;
		this.bsl = bsl;
	}
	
	@Override
	public Void execute(final ProgressManager progressManager) throws Exception {
		executeSubOperation(enterProgramModeOperation, progressManager.createSub(0.5f));
		try {
			bsl.writeFlash(getAddress(), getData(), getData().length);
		} finally {
			executeSubOperation(leaveProgramModeOperation, progressManager.createSub(0.5f));
		}
		return null;
	}

}
