package de.uniluebeck.itm.devicedriver.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.exception.TimeoutException;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.EraseFlashOperation;

public class PacemateEraseFlashOperation extends AbstractOperation<Void> implements EraseFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateEraseFlashOperation.class);
	
	private final PacemateDevice device;
	
	public PacemateEraseFlashOperation(PacemateDevice device) {
		this.device = device;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		// enableFlashErase();
		log.debug("Erasing flash");
		device.sendBootLoaderMessage(Messages.flashEraseRequestMessage(3, 14));

		device.receiveBootLoaderReply(Messages.CMD_SUCCESS);
		try {
			device.receiveBootLoaderReply(Messages.CMD_SUCCESS);
		} catch (TimeoutException e) {
			log.debug("one line erase response");
			throw e;
		}
		return null;
	}

}
