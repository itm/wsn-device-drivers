package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;

import javax.annotation.Nullable;


/**
 * OperationRunnable for getting the <code>MacAddress</code> of the given <code>MockConfiguration</code>.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class MockReadMacAddressOperation extends TimeLimitedOperation<MacAddress> implements ReadMacAddressOperation {

	private final MockConfiguration configuration;

	@Inject
	public MockReadMacAddressOperation(final TimeLimiter timeLimiter,
									   final MockConfiguration configuration,
									   @Assisted final long timeoutMillis,
									   @Assisted @Nullable final OperationListener<MacAddress> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.configuration = configuration;
	}

	@Override
	protected MacAddress callInternal() throws Exception {

		for (int i = 0; i < 10; i++) {
			Thread.sleep(100);
			progress(i * 0.1f);
		}

		return configuration.getMacAddress();
	}
}
