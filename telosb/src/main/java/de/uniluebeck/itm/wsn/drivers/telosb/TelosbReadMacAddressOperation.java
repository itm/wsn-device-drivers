package de.uniluebeck.itm.wsn.drivers.telosb;

import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;

public class TelosbReadMacAddressOperation extends AbstractOperation<MacAddress> implements ReadMacAddressOperation {

	@Override
	public MacAddress execute(final ProgressManager progressManager) throws Exception {
		throw new UnsupportedOperationException("Read mac address it not available.");
	}
}
