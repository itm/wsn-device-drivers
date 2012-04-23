package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteMacAddressOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Mock operation for writing a <code>MacAddress</code> in the given <code>MockConfiguration</code>.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class MockWriteMacAddressOperation extends TimeLimitedOperation<Void> implements WriteMacAddressOperation {

	private static final Logger log = LoggerFactory.getLogger(MockWriteMacAddressOperation.class);

	/**
	 * The <code>MockConfiguration</code> to which the <code>MacAddress</code> has to be assigned.
	 */
	private final MockConfiguration configuration;

	/**
	 * The <code>MacAddress</code> that has to be written to the configuration.
	 */
	private final MacAddress macAddress;

	@Inject
	public MockWriteMacAddressOperation(final TimeLimiter timeLimiter,
										final MockConfiguration configuration,
										@Assisted final MacAddress macAddress,
										@Assisted final long timeoutMillis,
										@Assisted final OperationListener<Void> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.macAddress = macAddress;
		this.configuration = configuration;
	}

	@Override
	protected Void callInternal() throws Exception {

		for (int i = 1; i <= 10 && !isCanceled(); ++i) {
			Thread.sleep(100);
			progress(i * 0.1f);
		}

		log.debug("Writing mac address: " + macAddress);
		configuration.setMacAddress(macAddress);
		return null;
	}
}
