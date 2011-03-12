package de.uniluebeck.itm.devicedriver.telosb;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractWriteFlashOperation;

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
