package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractWriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.serialport.Program;

public class TelosbWriteFlashOperation extends AbstractWriteFlashOperation {
	
	private final BSLTelosb bsl;
	
	@Inject
	public TelosbWriteFlashOperation(BSLTelosb bsl) {
		this.bsl = bsl;
	}
	
	@Override
	@Program
	public Void run(final ProgressManager progressManager, OperationContext context) throws Exception {
		bsl.writeFlash(getAddress(), getData(), getData().length);
		return null;
	}

}
