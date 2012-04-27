package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;

import javax.annotation.Nullable;

public class TelosbResetOperation extends TimeLimitedOperation<Void> implements ResetOperation {

	private final BSLTelosb bsl;

	@Inject
	public TelosbResetOperation(final TimeLimiter timeLimiter,
								final BSLTelosb bsl,
								@Assisted final long timeoutMillis,
								@Assisted @Nullable final OperationListener<Void> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.bsl = bsl;
	}

	@Override
	protected Void callInternal() throws Exception {
		bsl.reset();
		return null;
	}
}
