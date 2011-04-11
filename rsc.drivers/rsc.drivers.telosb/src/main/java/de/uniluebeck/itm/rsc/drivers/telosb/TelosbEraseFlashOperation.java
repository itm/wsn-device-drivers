package de.uniluebeck.itm.rsc.drivers.telosb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.rsc.drivers.core.exception.FlashEraseFailedException;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractProgressManager;
import de.uniluebeck.itm.rsc.drivers.core.operation.EraseFlashOperation;

public class TelosbEraseFlashOperation extends AbstractOperation<Void> implements EraseFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(TelosbEraseFlashOperation.class);

	private final BSLTelosb bsl;
	
	public TelosbEraseFlashOperation(BSLTelosb bsl) {
		this.bsl = bsl;
	}
	
	@Override
	public Void execute(AbstractProgressManager progressManager) throws Exception {
		byte[] reply = null;

		if (bsl == null) {
			throw new NullPointerException(
					"No connection to device (bsl is null).");
		}

		if (log.isDebugEnabled()) {
			log.debug("eraseFlash() (mass erase)");
		}

		// invoke boot loader
		if (!bsl.invokeBSL()) {
			throw new FlashEraseFailedException(
					"Mass erase failed: failed to invoke boot loader");
		}

		try {
			// send bsl command 'mass erase'
			bsl.sendBSLCommand(BSLTelosb.CMD_MASSERASE, 0xFFFF, 0xA506, null,
					false);

			// receive bsl reply
			reply = bsl.receiveBSLReply();
		} catch (Exception e) {
			throw new FlashEraseFailedException("Mass erase failed: " + e);
		}

		if ((reply[0] & 0xff) == BSLTelosb.DATA_NACK) {
			throw new FlashEraseFailedException(
					"Mass erase failed: received NACK");
		} else if (reply.length > 1) {
			throw new FlashEraseFailedException(
					"Mass erase failed: received unexpected response of length "
							+ reply.length);
		}

		// transmit default password to unlock protected commands after mass
		// erase
		try {
			if (!bsl.transmitPassword(null, false)) {
				log.warn("Received no ACK for password transmission after mass erase. Protected commands are still locked.");
			}
		} catch (Exception e) {
			throw new FlashEraseFailedException("Error transmitting default password after mass erase: " + e);
		}
		return null;
	}
}
