package de.uniluebeck.itm.wsn.drivers.jennic;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.exception.FlashEraseFailedException;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class JennicEraseFlashOperation extends TimeLimitedOperation<Void> implements EraseFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicEraseFlashOperation.class);

	private final JennicHelper helper;

	@Inject
	public JennicEraseFlashOperation(final TimeLimiter timeLimiter, final JennicHelper helper,
									 @Assisted final long timeoutMillis,
									 @Assisted @Nullable final OperationListener<Void> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.helper = helper;
	}

	@Override
	@SerialPortProgrammingMode
	protected Void callInternal() throws Exception {

		helper.sendBootloaderMessage(Messages.statusRegisterWriteMessage((byte) 0x00));
		progress(0.25f);

		byte[] response = helper.receiveBootloaderReply(Messages.WRITE_SR_RESPONSE);

		if (response[1] != 0x0) {
			log.error(String.format("Failed to write status register."));
			throw new FlashEraseFailedException();
		}

		if (isCanceled()) {
			return null;
		}

		progress(0.25f);
		log.trace("Erasing flash");
		helper.sendBootloaderMessage(Messages.flashEraseRequestMessage());
		response = helper.receiveBootloaderReply(Messages.FLASH_ERASE_RESPONSE);

		if (response[1] != 0x0) {
			throw new FlashEraseFailedException("Failed to erase flash.");
		}

		return null;
	}
}
