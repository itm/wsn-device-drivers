package de.uniluebeck.itm.rsc.drivers.telosb;

import de.uniluebeck.itm.rsc.drivers.core.MacAddress;
import de.uniluebeck.itm.rsc.drivers.core.Monitor;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.ReadMacAddressOperation;

public class TelosbReadMacAddressOperation extends AbstractOperation<MacAddress> implements ReadMacAddressOperation {

	@Override
	public MacAddress execute(final Monitor monitor) throws Exception {
		throw new UnsupportedOperationException("Read mac address it not available.");
	}
}
