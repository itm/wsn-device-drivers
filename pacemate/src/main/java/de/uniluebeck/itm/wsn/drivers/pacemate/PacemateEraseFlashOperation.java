package de.uniluebeck.itm.wsn.drivers.pacemate;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class PacemateEraseFlashOperation extends TimeLimitedOperation<Void> implements EraseFlashOperation {

	private static final Logger log = LoggerFactory.getLogger(PacemateEraseFlashOperation.class);

	private static final int START_ADDRESS = 3;

	private static final int END_ADDRESS = 14;

	private final PacemateHelper helper;

	@Inject
	public PacemateEraseFlashOperation(final TimeLimiter timeLimiter,
									   final PacemateHelper helper,
									   @Assisted final long timeoutMillis,
									   @Assisted @Nullable final OperationListener<Void> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.helper = helper;
	}

	@Override
	@SerialPortProgrammingMode
	protected Void callInternal() throws Exception {
		log.trace("Erasing whole flash...");
		helper.configureFlash(START_ADDRESS, END_ADDRESS);
		progress(0.25f);
		helper.eraseFlash(START_ADDRESS, END_ADDRESS);
		log.trace("Flash completely erased");
		return null;
	}
}
