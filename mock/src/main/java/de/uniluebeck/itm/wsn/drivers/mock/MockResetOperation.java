package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;

import javax.annotation.Nullable;


/**
 * Mock operation for resetting the connection.
 * Internal the periodical sending of messages is reset.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class MockResetOperation extends TimeLimitedOperation<Void> implements ResetOperation {

	private final MockDevice device;

	@Inject
	public MockResetOperation(final TimeLimiter timeLimiter,
							  final MockDevice device,
							  @Assisted final long timeoutMillis,
							  @Assisted @Nullable final OperationListener<Void> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.device = device;
	}

	@Override
	protected Void callInternal() throws Exception {
		Thread.sleep(100);
		progress(.3f);
		Thread.sleep(100);
		progress(.6f);
		device.reset();
		progress(1f);
		return null;
	}
}
