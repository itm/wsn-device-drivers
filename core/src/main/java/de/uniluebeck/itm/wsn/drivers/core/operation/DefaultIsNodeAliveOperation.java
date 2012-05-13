package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.Device;

import javax.annotation.Nullable;

public class DefaultIsNodeAliveOperation extends TimeLimitedOperation<Boolean> implements IsNodeAliveOperation {

	private final Device device;

	@Inject
	public DefaultIsNodeAliveOperation(final TimeLimiter timeLimiter,
									   final Device device,
									   @Assisted final long timeoutMillis,
									   @Assisted @Nullable final OperationListener<Boolean> listener) {
		super(timeLimiter, timeoutMillis, listener);
		this.device = device;
	}

	@Override
	protected Boolean callInternal() throws Exception {
		return device.isConnected();
	}
}
