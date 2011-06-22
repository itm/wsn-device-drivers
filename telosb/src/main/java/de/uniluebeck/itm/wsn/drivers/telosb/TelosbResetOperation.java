package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;

public class TelosbResetOperation extends AbstractOperation<Void> implements ResetOperation {

	private final BSLTelosb bsl;
	
	@Inject
	public TelosbResetOperation(BSLTelosb bsl) {
		this.bsl = bsl;
	}
	
	@Override
	public Void execute(ProgressManager progressManager) throws Exception {
		bsl.reset();
		return null;
	}

}
