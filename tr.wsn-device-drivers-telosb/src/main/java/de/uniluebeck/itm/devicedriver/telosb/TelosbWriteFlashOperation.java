package de.uniluebeck.itm.devicedriver.telosb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.exception.FlashProgramFailedException;
import de.uniluebeck.itm.devicedriver.operation.AbstractWriteFlashOperation;

public class TelosbWriteFlashOperation extends AbstractWriteFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(TelosbWriteFlashOperation.class);
	
	private final BSLTelosb bsl;
	
	public TelosbWriteFlashOperation(BSLTelosb bsl) {
		this.bsl = bsl;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		// verify if block range is erased
		if (!bsl.verifyBlock(address, length, null)) {
			throw new FlashProgramFailedException("Failed to program flash: block range is not erased completely");
		}
		log.debug(String.format("***Programming data block at address: 0x%02x, writing %d bytes.", address, data.length));

		// execute bsl patch first(only for BSL version <=1.10)
		bsl.executeBSLPatch();

		// program block
		bsl.sendBSLCommand(BSLTelosb.CMD_TXDATABLOCK, address, length, data, false);

		byte[] reply = bsl.receiveBSLReply();
		if ((reply[0] & 0xFF) != BSLTelosb.DATA_ACK) {
			throw new FlashProgramFailedException(
					"Failed to program flash: received no ACK");
		}

		// verify programmed block
		if (!bsl.verifyBlock(address, length, data)) {
			throw new FlashProgramFailedException("Failed to program flash: verification of written data failed");
		}
		return null;
	}

}
