package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractUnsupportedOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;

import javax.annotation.Nullable;

public class TriSOSGetChipTypeOperation extends AbstractUnsupportedOperation<ChipType> implements GetChipTypeOperation {

	@Inject
	public TriSOSGetChipTypeOperation(final TimeLimiter timeLimiter, @Assisted final long timeoutMillis,
									 @Assisted @Nullable final OperationListener<ChipType> chipTypeOperationListener) {
		super(timeLimiter, timeoutMillis, chipTypeOperationListener);
	}
}
