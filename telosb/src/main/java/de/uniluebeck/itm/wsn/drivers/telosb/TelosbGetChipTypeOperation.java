package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;

import javax.annotation.Nullable;

public class TelosbGetChipTypeOperation extends TimeLimitedOperation<ChipType> implements GetChipTypeOperation {

	@Inject
	public TelosbGetChipTypeOperation(final TimeLimiter timeLimiter,
									  @Assisted final long timeoutMillis,
									  @Assisted @Nullable final OperationListener<ChipType> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
	}

	@Override
	protected ChipType callInternal() throws Exception {
		return ChipType.TelosB;
	}
}
