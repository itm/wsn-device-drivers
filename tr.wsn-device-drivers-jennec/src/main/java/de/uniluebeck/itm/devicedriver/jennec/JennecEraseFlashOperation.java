package de.uniluebeck.itm.devicedriver.jennec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.exception.FlashEraseFailedException;
import de.uniluebeck.itm.devicedriver.operation.AbstractEraseFlashOperation;

public class JennecEraseFlashOperation extends AbstractEraseFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennecEraseFlashOperation.class);
	
	private final JennecDevice device;
	
	public JennecEraseFlashOperation(JennecDevice device) {
		this.device = device;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		device.sendBootLoaderMessage(Messages.statusRegisterWriteMessage((byte) 0x00));

		byte[] response = device.receiveBootLoaderReply(Messages.WRITE_SR_RESPONSE);

		if (response[1] != 0x0) {
			log.error(String.format("Failed to write status register."));
			throw new FlashEraseFailedException();
		}
		
		if (isCanceled()) {
			return null;
		}
		
		log.debug("Erasing flash");
		device.sendBootLoaderMessage(Messages.flashEraseRequestMessage());
		response = device.receiveBootLoaderReply(Messages.FLASH_ERASE_RESPONSE);

		if (response[1] != 0x0) {
			log.error("Failed to erase flash.");
			throw new FlashEraseFailedException();
		}
		return null;
	}

}
