package de.uniluebeck.itm.rsc.drivers.telosb;

import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractProgressManager;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractWriteFlashOperation;

public class TelosbWriteFlashOperation extends AbstractWriteFlashOperation {
	
	private final TelosbDevice device;
	
	public TelosbWriteFlashOperation(final TelosbDevice device) {
		this.device = device;
	}
	
	@Override
	public Void execute(final AbstractProgressManager progressManager) throws Exception {
		executeSubOperation(device.createEnterProgramModeOperation(), progressManager.createSub(0.5f));
		try {
			device.writeFlash(getAddress(), getData(), getData().length);
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), progressManager.createSub(0.5f));
		}
		return null;
	}

}
