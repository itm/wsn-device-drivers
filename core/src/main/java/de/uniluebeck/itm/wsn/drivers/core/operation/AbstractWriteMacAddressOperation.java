package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.common.util.concurrent.TimeLimiter;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;

import javax.annotation.Nullable;


/**
 * Abstract operation for writing the mac address to the device.
 * Stores the <code>MacAddress</code> internally. Accessible with a getter method.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public abstract class AbstractWriteMacAddressOperation extends TimeLimitedOperation<Void>
		implements WriteMacAddressOperation {

	/**
	 * The <code>MacAddress</code> that will be written to the device.
	 */
	protected MacAddress macAddress;

	protected AbstractWriteMacAddressOperation(final TimeLimiter timeLimiter,
											   final MacAddress macAddress,
											   final long timeout,
											   @Nullable final OperationListener<Void> operationCallback) {
		super(timeLimiter, timeout, operationCallback);
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
