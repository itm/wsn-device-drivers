package de.uniluebeck.itm.wsn.drivers.core.operation;

import de.uniluebeck.itm.wsn.drivers.core.MacAddress;

/**
 * Interface that defines an <code>OperationRunnable</code> that write a <code>MacAddress</code> to the device.
 * 
 * @author Malte Legenhausen
 */
public interface WriteMacAddressOperation extends OperationRunnable<Void> {

	/**
	 * Sets the <code>MacAddress</code> that has to be written to the device.
	 * 
	 * @param macAddress The <code>MacAddress</code> that has to be written.
	 */
	void setMacAddress(MacAddress macAddress);
}
