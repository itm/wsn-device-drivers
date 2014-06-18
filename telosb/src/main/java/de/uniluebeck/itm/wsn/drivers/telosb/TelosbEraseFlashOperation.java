package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.exception.FlashEraseFailedException;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class TelosbEraseFlashOperation extends TimeLimitedOperation<Void> implements EraseFlashOperation {

	private static final Logger log = LoggerFactory.getLogger(TelosbEraseFlashOperation.class);

	private final BSLTelosb bsl;

	@Inject
	public TelosbEraseFlashOperation(final TimeLimiter timeLimiter,
									 final BSLTelosb bsl,
									 @Assisted final long timeoutMillis,
									 @Assisted @Nullable final OperationListener<Void> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.bsl = bsl;
	}

	@Override
	protected Void callInternal() throws Exception {

		byte[] reply;

		if (bsl == null) {
			throw new NullPointerException("No connection to device (bsl is null).");
		}

		if (log.isDebugEnabled()) {
			log.debug("eraseFlash() (mass erase)");
		}

		// invoke boot loader
		if (!bsl.invokeBSL()) {
			throw new FlashEraseFailedException("Mass erase failed: failed to invoke boot loader");
		}

		try {
			// send bsl command 'mass erase'
			bsl.sendBSLCommand(BSLTelosb.CMD_MASSERASE, 0xFF00, 0xA506, null, false);

			// receive bsl reply
			reply = bsl.receiveBSLReply();

		} catch (Exception e) {
			throw new FlashEraseFailedException("Mass erase failed: " + e);
		}

		if ((reply[0] & 0xff) == BSLTelosb.DATA_NACK) {
			throw new FlashEraseFailedException("Mass erase failed: received NACK");
		} else if (reply.length > 1) {
			throw new FlashEraseFailedException(
					"Mass erase failed: received unexpected response of length " + reply.length
			);
		}

		// transmit default password to unlock protected commands after mass
		// erase
		try {
			if (!bsl.transmitPassword(null, false)) {
				log.warn(
						"Received no ACK for password transmission after mass erase. Protected commands are still locked."
				);
			}
		} catch (Exception e) {
			throw new FlashEraseFailedException("Error transmitting default password after mass erase: " + e);
		}

		return null;
	}
}
