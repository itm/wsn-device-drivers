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
	
	@Override
	public ChipType execute(Monitor monitor) throws Exception {
		// Send chip type read request
		device.sendBootLoaderMessage(Messages.ReadPartIDRequestMessage());

		// Read chip type read response
		final byte[] response = device.receiveBootLoaderReply(Messages.CMD_SUCCESS);

		ChipType chipType = ChipType.UNKNOWN;
		int chipid = 0;

		if (response.length > 6) {
			int i = 6;
			while ((i < response.length) && (response[i] != 0xd)) {
				chipid = chipid * 10;
				chipid = chipid + (response[i] - 0x30);
				i++;
			}
		}

		if (chipid == 196387)
			chipType = ChipType.LPC2136;
		else {
			log.error("Defaulted to chip type LPC2136 (Pacemate). Identification may be wrong." + chipid);
			chipType = ChipType.LPC2136;
		}

		log.debug("Chip identified as " + chipType + " (received " + chipid + ")");
		return chipType;
	}

}
