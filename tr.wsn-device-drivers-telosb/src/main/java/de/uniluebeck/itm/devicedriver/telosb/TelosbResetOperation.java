package de.uniluebeck.itm.devicedriver.telosb;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractResetOperation;

public class TelosbResetOperation extends AbstractResetOperation {

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
