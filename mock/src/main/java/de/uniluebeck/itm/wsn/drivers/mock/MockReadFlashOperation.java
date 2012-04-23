package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;

import javax.annotation.Nullable;


/**
 * Mock operation for reading data from the emulated flash rom in the configuration.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class MockReadFlashOperation extends TimeLimitedOperation<byte[]> implements ReadFlashOperation {

	/**
	 * The start address of the read operation.
	 */
	private final int address;

	/**
	 * The length of the data that has to be read.
	 */
	private final int length;

	/**
	 * The <code>MockConfiguration</code> of the <code>MockDevice</code>.
	 */
	private final MockConfiguration configuration;

	@Inject
	public MockReadFlashOperation(final TimeLimiter timeLimiter,
								  final MockConfiguration configuration,
								  @Assisted("address") int address,
								  @Assisted("length") int length,
								  @Assisted final long timeoutMillis,
								  @Assisted @Nullable final OperationListener<byte[]> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.configuration = configuration;
		this.address = address;
		this.length = length;
	}

	@Override
	protected byte[] callInternal() throws Exception {

		for (int i = 1; i <= 10 && !isCanceled(); ++i) {
			Thread.sleep(100);
			progress(i * 0.1f);
		}

		final byte[] result = new byte[length];
		System.arraycopy(configuration.getFlashRom(), address, result, 0, length);
		return result;
	}
}
