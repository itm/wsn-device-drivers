package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.exception.NotImplementedException;
import de.uniluebeck.itm.devicedriver.operation.AbstractSendOperation;

public class MockSendOperation extends AbstractSendOperation {

	@Override
	public Void execute(Monitor monitor) throws Exception {
		throw new NotImplementedException("This operation is not available for MockDevice");
	}
}
