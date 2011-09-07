package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;

public class TelosbResetOperation implements ResetOperation {

	private final BSLTelosb bsl;
	
	@Inject
	public TelosbResetOperation(BSLTelosb bsl) {
		this.bsl = bsl;
	}
	
	@Override
	public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
		bsl.reset();
		return null;
	}

}
