package de.uniluebeck.itm.devicedriver.operation;

import de.uniluebeck.itm.devicedriver.MacAddress;

public abstract class AbstractWriteMacAddressOperation extends AbstractOperation<Void> implements WriteMacAddressOperation {

	private MacAddress macAddress;
	
	@Override
	public void setMacAddress(final MacAddress macAddress) {
		this.macAddress = macAddress;
	}
	
	public MacAddress getMacAddress() {
		return macAddress;
	}
}
