package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractUnsupportedOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;

import javax.annotation.Nullable;

public class TriSOSReadFlashOperation extends AbstractUnsupportedOperation<byte[]> implements ReadFlashOperation {

	@Inject
	public TriSOSReadFlashOperation(final TimeLimiter timeLimiter, @Assisted final long timeoutMillis,
                                                                       @Assisted @Nullable final OperationListener<byte[]> operationListener) {
		super(timeLimiter, timeoutMillis, operationListener);
	}
}
