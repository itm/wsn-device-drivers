package de.uniluebeck.itm.devicedriver.telosb;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.exception.FlashReadFailedException;
import de.uniluebeck.itm.devicedriver.operation.AbstractReadFlashOperation;

public class TelosbReadFlashOperation extends AbstractReadFlashOperation {

	private final BSLTelosb bsl;
	
	public TelosbReadFlashOperation(BSLTelosb bsl) {
		this.bsl = bsl;
	}
	
	@Override
	public byte[] execute(Monitor monitor) throws Exception {
		byte[] reply = null;

		try {
			// execute bsl patch
			bsl.executeBSLPatch();

			// receive data block
			bsl.sendBSLCommand(BSLTelosb.CMD_RXDATABLOCK, address, length, null, false);

			// receive reply
			reply = bsl.receiveBSLReply();
		} catch (Exception e) {
			throw new FlashReadFailedException(String.format(
					"Failed to read flash " + "at address 0x%02x, %d bytes: "
							+ e + ".", address, length));
		}

		if ((0xFF & reply[0]) != BSLTelosb.DATA_ACK) {
			throw new FlashReadFailedException(String.format(
					"Failed to read flash "
							+ "at address 0x%02x, %d bytes (missing BSL ACK).",
					address, length));
		}
		return null;
	}

}
