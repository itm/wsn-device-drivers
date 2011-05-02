package de.uniluebeck.itm.wsn.drivers.telosb;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractWriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class TelosbWriteFlashOperation extends AbstractWriteFlashOperation {
	
	private final TelosbDevice device;
	
	public TelosbWriteFlashOperation(final TelosbDevice device) {
		this.device = device;
	}
	
	@Override
	public Void execute(final ProgressManager progressManager) throws Exception {
		executeSubOperation(device.createEnterProgramModeOperation(), progressManager.createSub(0.5f));
		try {
			device.writeFlash(getAddress(), getData(), getData().length);
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), progressManager.createSub(0.5f));
		}
		return null;
	}

}
