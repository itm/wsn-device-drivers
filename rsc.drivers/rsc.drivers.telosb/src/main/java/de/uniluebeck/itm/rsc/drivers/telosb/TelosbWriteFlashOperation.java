package de.uniluebeck.itm.rsc.drivers.telosb;

import de.uniluebeck.itm.rsc.drivers.core.Monitor;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractWriteFlashOperation;

public class TelosbWriteFlashOperation extends AbstractWriteFlashOperation {
	
	private final TelosbDevice device;
	
	public TelosbWriteFlashOperation(final TelosbDevice device) {
		this.device = device;
	}
	
	@Override
	public Void execute(final Monitor monitor) throws Exception {
		executeSubOperation(device.createEnterProgramModeOperation(), monitor);
		try {
			device.writeFlash(getAddress(), getData(), getData().length);
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), monitor);
		}
		return null;
	}

}
