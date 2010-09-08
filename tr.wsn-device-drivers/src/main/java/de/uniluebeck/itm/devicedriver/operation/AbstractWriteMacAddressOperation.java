package de.uniluebeck.itm.devicedriver.operation;

import de.uniluebeck.itm.devicedriver.MacAddress;

public abstract class AbstractWriteMacAddressOperation extends AbstractOperation<Void> implements WriteMacAddressOperation {

	protected MacAddress macAddress;
	
	@Override
	public void setMacAddress(MacAddress macAddress) {
		this.macAddress = macAddress;
	}
}
