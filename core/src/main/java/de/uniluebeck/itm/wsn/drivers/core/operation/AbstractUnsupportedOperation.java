package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.common.util.concurrent.TimeLimiter;

import javax.annotation.Nullable;

public abstract class AbstractUnsupportedOperation<T> extends TimeLimitedOperation<T> implements Operation<T> {


	public AbstractUnsupportedOperation(final TimeLimiter timeLimiter,
										final long timeoutMillis,
										@Nullable final OperationListener<T> tOperationListener) {
		super(timeLimiter, timeoutMillis, tOperationListener);
	}

	@Override
	protected T callInternal() throws Exception {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " is not supported!");
	}
}
