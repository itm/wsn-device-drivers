package de.uniluebeck.itm.rsc.drivers.core.operation;

import de.uniluebeck.itm.rsc.drivers.core.MacAddress;


/**
 * Abstract operation for writing the mac address to the device.
 * Stores the <code>MacAddress</code> internally. Accessable with a getter method.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractWriteMacAddressOperation extends AbstractOperation<Void> implements WriteMacAddressOperation {

	/**
	 * The <code>MacAddress</code> that will be written to the device.
	 */
	private MacAddress macAddress;
	
	@Override
	public void setMacAddress(final MacAddress macAddress) {
		this.macAddress = macAddress;
	}
	
	/**
	 * Getter for the <code>MacAddress</code>.
	 * 
	 * @return The <code>MacAddress</code>.
	 */
	public MacAddress getMacAddress() {
		return macAddress;
	}
}
