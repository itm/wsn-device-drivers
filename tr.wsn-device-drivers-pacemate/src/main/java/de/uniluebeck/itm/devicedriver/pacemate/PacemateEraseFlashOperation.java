package de.uniluebeck.itm.devicedriver.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.EraseFlashOperation;

public class PacemateEraseFlashOperation extends AbstractOperation<Void> implements EraseFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateEraseFlashOperation.class);
	
	private static final int START_ADDRESS = 3;
	
	private static final int END_ADDRESS = 14;
	
	private final PacemateDevice device;
	
	public PacemateEraseFlashOperation(PacemateDevice device) {
		this.device = device;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		log.debug("Erasing whole flash...");
		monitor.onProgressChange(0.0f);
		device.configureFlash(START_ADDRESS, END_ADDRESS);
		monitor.onProgressChange(0.25f);
		device.eraseFlash(START_ADDRESS, END_ADDRESS);
		monitor.onProgressChange(1.0f);
		log.debug("Flash completly erased");
		return null;
	}

}
