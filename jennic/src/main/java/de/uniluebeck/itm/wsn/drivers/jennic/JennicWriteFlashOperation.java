package de.uniluebeck.itm.wsn.drivers.jennic;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractWriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class JennicWriteFlashOperation extends AbstractWriteFlashOperation {

	private static final Logger log = LoggerFactory.getLogger(JennicWriteFlashOperation.class);

	private final JennicHelper helper;

	@Inject
	public JennicWriteFlashOperation(final TimeLimiter timeLimiter,
									 final JennicHelper helper,
									 @Assisted("address") int address,
									 @Assisted byte[] data,
									 @Assisted("length") int length,
									 @Assisted final long timeoutMillis,
									 @Assisted @Nullable final OperationListener<Void> operationCallback) {
		super(timeLimiter, address, data, length, timeoutMillis, operationCallback);
		this.helper = helper;
	}

	@Override
	@SerialPortProgrammingMode
	protected Void callInternal() throws Exception {
		log.trace("Writing to flash...");
		helper.writeFlash(getAddress(), getData());
		log.trace("Flash written");
		return null;
	}
}
