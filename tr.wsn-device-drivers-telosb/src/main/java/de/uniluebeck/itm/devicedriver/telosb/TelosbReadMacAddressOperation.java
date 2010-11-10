package de.uniluebeck.itm.devicedriver.telosb;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.exception.NotImplementedException;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadMacAddressOperation;

public class TelosbReadMacAddressOperation extends
		AbstractOperation<MacAddress> implements ReadMacAddressOperation {

	@Override
	public MacAddress execute(Monitor monitor) throws Exception {
		throw new NotImplementedException("Read mac address it not available.");
	}
}
