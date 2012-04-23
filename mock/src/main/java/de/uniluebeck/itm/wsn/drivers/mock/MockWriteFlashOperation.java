package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;

import javax.annotation.Nullable;


/**
 * Mock operation for writing data in the simulated flash rom.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class MockWriteFlashOperation extends TimeLimitedOperation<Void> implements WriteFlashOperation {

	/**
	 * Start address for writing in the flash.
	 */
	private final int address;

	/**
	 * The data that has to be written.
	 */
	private final byte[] data;

	/**
	 * The length of the data.
	 */
	private final int length;

	/**
	 * The <code>MockConfiguration</code> of the <code>MockDevice</code>.
	 */
	private final MockConfiguration configuration;

	@Inject
	public MockWriteFlashOperation(final TimeLimiter timeLimiter,
								   final MockConfiguration configuration,
								   @Assisted("address") int address,
								   @Assisted byte[] data,
								   @Assisted("length") int length,
								   @Assisted final long timeoutMillis,
								   @Assisted @Nullable final OperationListener<Void> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.address = address;
		this.data = data;
		this.length = length;
		this.configuration = configuration;
	}

	@Override
	protected Void callInternal() throws Exception {

		for (int i = 0; i < 10; ++i) {
			Thread.sleep(1000);
			progress(i * 0.1f);
		}

		System.arraycopy(data, 0, configuration.getFlashRom(), address, length);

		return null;
	}
}
