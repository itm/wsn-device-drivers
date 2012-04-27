package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractUnsupportedOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;

import javax.annotation.Nullable;

public class TriSOSReadFlashOperation extends AbstractUnsupportedOperation<byte[]> implements ReadFlashOperation {

	@Inject
	public TriSOSReadFlashOperation(final TimeLimiter timeLimiter, final long timeoutMillis,
									@Nullable final OperationListener<byte[]> operationListener) {
		super(timeLimiter, timeoutMillis, operationListener);
	}
}
