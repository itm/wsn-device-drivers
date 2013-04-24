package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractUnsupportedOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;

import javax.annotation.Nullable;

public class TriSOSWriteFlashOperation extends AbstractUnsupportedOperation<Void> implements WriteFlashOperation {

	@Inject
	public TriSOSWriteFlashOperation(final TimeLimiter timeLimiter, @Assisted final long timeoutMillis,
									@Assisted @Nullable final OperationListener<Void> voidOperationListener) {
		super(timeLimiter, timeoutMillis, voidOperationListener);
	}
}
