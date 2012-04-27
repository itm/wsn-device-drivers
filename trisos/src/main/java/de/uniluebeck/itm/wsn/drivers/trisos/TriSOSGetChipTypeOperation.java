package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractUnsupportedOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;

import javax.annotation.Nullable;

public class TriSOSGetChipTypeOperation extends AbstractUnsupportedOperation<ChipType> implements GetChipTypeOperation {

	@Inject
	public TriSOSGetChipTypeOperation(final TimeLimiter timeLimiter, final long timeoutMillis,
									  @Nullable final OperationListener<ChipType> chipTypeOperationListener) {
		super(timeLimiter, timeoutMillis, chipTypeOperationListener);
	}
}
