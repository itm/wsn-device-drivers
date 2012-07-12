package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;

import javax.annotation.Nullable;


/**
 * Returns the <code>ChipType</code> set in the given <code>MockConfiguration</code>.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class MockGetChipTypeOperation extends TimeLimitedOperation<ChipType> implements GetChipTypeOperation {

	/**
	 * The <code>MockConfiguration</code> from which the <code>ChipType</code> has to be received.
	 */
	private final MockConfiguration configuration;

	@Inject
	public MockGetChipTypeOperation(final TimeLimiter timeLimiter,
									final MockConfiguration configuration,
									@Assisted final long timeoutMillis,
									@Assisted @Nullable final OperationListener<ChipType> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.configuration = configuration;
	}

	@Override
	@SerialPortProgrammingMode
	protected ChipType callInternal() throws Exception {
		Thread.sleep(100);
		progress(1f);
		return configuration.getChipType();
	}
}
