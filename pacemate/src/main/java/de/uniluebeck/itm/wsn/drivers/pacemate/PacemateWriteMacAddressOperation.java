package de.uniluebeck.itm.wsn.drivers.pacemate;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractUnsupportedOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteMacAddressOperation;

import javax.annotation.Nullable;

public class PacemateWriteMacAddressOperation extends AbstractUnsupportedOperation<Void> implements
		WriteMacAddressOperation {

	@Inject
	public PacemateWriteMacAddressOperation(final TimeLimiter timeLimiter,
											@Assisted final long timeoutMillis,
											@Assisted @Nullable final OperationListener<Void> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
	}
}
