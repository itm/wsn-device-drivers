package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractUnsupportedOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;

import javax.annotation.Nullable;

public class TriSOSEraseFlashOperation extends AbstractUnsupportedOperation<Void> implements EraseFlashOperation {

	@Inject
	public TriSOSEraseFlashOperation(final TimeLimiter timeLimiter, final long timeoutMillis,
									 @Nullable final OperationListener<Void> voidOperationListener) {
		super(timeLimiter, timeoutMillis, voidOperationListener);
	}
}
