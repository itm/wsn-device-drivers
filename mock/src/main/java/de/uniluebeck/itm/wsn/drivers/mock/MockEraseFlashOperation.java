package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.operation.*;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;

import javax.annotation.Nullable;


/**
 * This operation fills the byte array of the given configuration with 0x00.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class MockEraseFlashOperation extends TimeLimitedOperation<Void> implements EraseFlashOperation {

	/**
	 * The configuration that contains the byte array that has to be erased.
	 */
	private final MockConfiguration configuration;

	@Inject
	public MockEraseFlashOperation(final TimeLimiter timeLimiter,
								   final MockConfiguration configuration,
								   @Assisted final long timeoutMillis,
								   @Assisted @Nullable final OperationListener<Void> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.configuration = configuration;
	}

	@Override
	@SerialPortProgrammingMode
	protected Void callInternal() throws Exception {
		final byte[] flashRom = configuration.getFlashRom();
		final float worked = 1.0f / flashRom.length;
		for (int i = 0; i < flashRom.length; ++i) {
			Thread.sleep(100);
			flashRom[i] = 0x00;
			progress(worked);
		}
		configuration.setFlashRom(flashRom);
		return null;
	}
}
