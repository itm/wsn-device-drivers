package de.uniluebeck.itm.rsc.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.rsc.drivers.core.ChipType;
import de.uniluebeck.itm.rsc.drivers.core.Monitor;
import de.uniluebeck.itm.rsc.drivers.core.exception.RamReadFailedException;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.tr.util.StringUtils;

public class JennicGetChipTypeOperation extends AbstractOperation<ChipType> implements GetChipTypeOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicGetChipTypeOperation.class);
	
	private final JennicDevice device;
	
	public JennicGetChipTypeOperation(JennicDevice device) {
		this.device = device;
	}
	
	private ChipType determineChipType(byte s, byte t) {
		ChipType chipType = ChipType.UNKNOWN;

		if (s == 0x00 && t == 0x20)
			chipType = ChipType.JN513X;
		else if (s == 0x10 && t == 0x00)
			chipType = ChipType.JN513XR1;
		else if (s == 0x20 && t == 0x00)
			chipType = ChipType.JN5121;
		else {
			log.warn("Defaulted to chip type JN5121. Identification may be wrong.");
			chipType = ChipType.JN5121;
		}
		return chipType;
	}
	
	private ChipType getChipType(final Monitor monitor) throws Exception {
		// Send chip type read request
		device.sendBootLoaderMessage(Messages.ramReadRequestMessage(0x100000FC, 0x0004));

		// Read chip type read response
		final byte[] response = device.receiveBootLoaderReply(Messages.RAM_READ_RESPONSE);

		// Throw error if reading failed
		if (response[1] != 0x00) {
			log.error(String.format("Failed to read chip type from RAM: Response should be 0x00, yet it is: 0x%02x", response[1]));
			throw new RamReadFailedException();
		}

		final ChipType chipType = determineChipType(response[2], response[3]);

		log.trace("Chip identified as " + chipType + " (received " + StringUtils.toHexString(response[2]) + " "
				+ StringUtils.toHexString(response[3]) + ")");
		return chipType;
	}
	
	@Override
	public ChipType execute(Monitor monitor) throws Exception {
		ChipType chipType = null;
		executeSubOperation(device.createEnterProgramModeOperation(), monitor);
		try {
			chipType = getChipType(monitor);
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), monitor);
		}
		return chipType;
	}

}
