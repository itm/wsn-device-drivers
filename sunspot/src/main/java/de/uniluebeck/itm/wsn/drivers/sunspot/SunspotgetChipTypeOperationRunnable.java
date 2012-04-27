package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;

import javax.annotation.Nullable;

public class SunspotGetChipTypeOperationRunnable extends TimeLimitedOperation<ChipType>
		implements GetChipTypeOperation {

	@Inject
	public SunspotGetChipTypeOperationRunnable(final TimeLimiter timeLimiter,
											   @Assisted final long timeoutMillis,
											   @Assisted
											   @Nullable
											   final OperationListener<ChipType> operationListener) {
		super(timeLimiter, timeoutMillis, operationListener);
	}

	@Override
	protected ChipType callInternal() throws Exception {
		return ChipType.SUNSPOT;
	}
}
