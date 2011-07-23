package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractWriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
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
	public Void run(final ProgressManager progressManager, OperationContext context) throws Exception {
		context.execute(enterProgramModeOperation, progressManager.createSub(0.5f));
		try {
			bsl.writeFlash(getAddress(), getData(), getData().length);
		} finally {
			context.execute(leaveProgramModeOperation, progressManager.createSub(0.5f));
		}
		return null;
	}

}
