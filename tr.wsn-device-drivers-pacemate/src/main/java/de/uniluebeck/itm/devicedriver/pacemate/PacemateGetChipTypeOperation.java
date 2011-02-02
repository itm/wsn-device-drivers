package de.uniluebeck.itm.devicedriver.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;

public class PacemateGetChipTypeOperation extends AbstractOperation<ChipType> implements GetChipTypeOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateGetChipTypeOperation.class);
	
	private final PacemateDevice device;
	
	public PacemateGetChipTypeOperation(PacemateDevice device) {
		this.device = device;
	}
	
	private ChipType getChipType() throws Exception {
		device.clearStreamData();
		device.autobaud();

		// Wait for a connection
		while (!isCanceled() && !device.waitForConnection()) {
			log.info("Still waiting for a connection");
		}

		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			return null;
		}
		
		// Send chip type read request
		device.sendBootLoaderMessage(Messages.ReadPartIDRequestMessage());

		// Read chip type read response
		final String response = device.receiveBootLoaderReplySuccess(Messages.CMD_SUCCESS);

		ChipType chipType = ChipType.UNKNOWN;

		if (response.compareTo("196387") == 0) {
			chipType = ChipType.LPC2136;
		} else {
			log.error("Defaulted to chip type LPC2136 (Pacemate). Identification may be wrong." + response);
			chipType = ChipType.LPC2136;
		}

		log.debug("Chip identified as " + chipType + " (received " + response + ")");
		return chipType;
	}
	
	@Override
	public ChipType execute(Monitor monitor) throws Exception {
		executeSubOperation(device.createEnterProgramModeOperation());
		ChipType chipType = ChipType.UNKNOWN;
		try {
			chipType = getChipType();
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation());
		}
		return chipType;
	}

}
