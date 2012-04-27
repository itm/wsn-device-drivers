package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractWriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;

import javax.annotation.Nullable;

public class TelosbWriteFlashOperation extends AbstractWriteFlashOperation {

	private final BSLTelosb bsl;

	@Inject
	public TelosbWriteFlashOperation(final TimeLimiter timeLimiter,
									 final BSLTelosb bsl,
									 @Assisted("address") int address,
									 @Assisted byte[] data,
									 @Assisted("length") int length,
									 @Assisted final long timeoutMillis,
									 @Assisted @Nullable final OperationListener<Void> operationCallback) {
		super(timeLimiter, address, data, length, timeoutMillis, operationCallback);
		this.bsl = bsl;
	}

	@Override
	@SerialPortProgrammingMode
	protected Void callInternal() throws Exception {
		bsl.writeFlash(getAddress(), getData(), getData().length);
		return null;
	}
}
