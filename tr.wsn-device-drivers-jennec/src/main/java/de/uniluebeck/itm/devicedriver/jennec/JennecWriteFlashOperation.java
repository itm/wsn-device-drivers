package de.uniluebeck.itm.devicedriver.jennec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.exception.FlashProgramFailedException;
import de.uniluebeck.itm.devicedriver.operation.AbstractWriteFlashOperation;

public class JennecWriteFlashOperation extends AbstractWriteFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennecWriteFlashOperation.class);
	
	private final JennecDevice device;
	
	public JennecWriteFlashOperation(JennecDevice device) {
		this.device = device;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		// Send flash program request
		// log.debug("Sending program request for address " + address + " with " + data.length + " bytes");
		device.sendBootLoaderMessage(Messages.flashProgramRequestMessage(address, data));

		// Read flash program response
		byte[] response = device.receiveBootLoaderReply(Messages.FLASH_PROGRAM_RESPONSE);

		// Throw error if writing failed
		if (response[1] != 0x0) {
			log.error(String.format("Failed to write to flash: Response should be 0x00, yet it is: 0x%02x", response[1]));
			throw new FlashProgramFailedException();
		} else {
			log.debug("Received Ack");
		}
		return null;
	}

}
