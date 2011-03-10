package de.uniluebeck.itm.devicedriver.telosb;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.ResetOperation;

public class TelosbResetOperation extends AbstractOperation<Void> implements ResetOperation {

	private final BSLTelosb bsl;
	
	public TelosbResetOperation(BSLTelosb bsl) {
		this.bsl = bsl;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		bsl.reset();
		return null;
	}

}
