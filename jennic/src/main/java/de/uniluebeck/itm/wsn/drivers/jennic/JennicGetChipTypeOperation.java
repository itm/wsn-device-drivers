package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.exception.RamReadFailedException;
import de.uniluebeck.itm.wsn.drivers.core.exception.UnexpectedResponseException;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.serialport.Program;

public class JennicGetChipTypeOperation	implements GetChipTypeOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicGetChipTypeOperation.class);

	private final JennicHelper helper;

	@Inject
	public JennicGetChipTypeOperation(JennicHelper helper) {
		this.helper = helper;
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

	@Override
	@Program
	public ChipType run(final ProgressManager progressManager, OperationContext context) throws Exception {
		log.trace("Getting ChipType...");

		ChipType chipType = ChipType.UNKNOWN;
		
		helper.sendBootLoaderMessage(Messages.chipIdMessage());

		try {
			// Read chip type read response
			byte[] res = helper.receiveBootLoaderReply(Messages.CHIP_ID_RESPONSE);
			String S = "received: (len=" + res.length + ") ";
			for (int i = 0; i < res.length; i++) {
				S = S + "res[" + i + "]=" + StringUtils.toHexString(res[i]) + " ";
			}
			log.debug(S);
			if (res.length == 6) {
				if (((res[1] == 0) && (res[2] == 0x10) && (res[3] == 0x40)
						&& (res[4] == 0x46) && (res[5] == (byte) 0x86))
						|| ((res[1] == 0) && (res[2] == 0x10)
								&& (res[3] == (byte) 0x80) && (res[4] == 0x46) && (res[5] == (byte) 0x86))) {
					chipType = ChipType.JN5148;
					log.debug("Chip identified as " + chipType + ".");
				} else
					log.error("BYTES incorrect res[1]=" + res[1] + " != " + 0
							+ "res[2]=" + res[2] + " != " + 0x10 + "res[3]="
							+ res[3] + " != " + 0x40 + "res[4]=" + res[4]
							+ " != " + 0x46 + "res[5]=" + res[5] + " != "
							+ (byte) 0x86);
			} else
				log.error("length incorrect");
		} catch (UnexpectedResponseException e) {

			// Send chip type read request
			helper.sendBootLoaderMessage(Messages.ramReadRequestMessage(
					0x100000FC, 0x0004));

			// Read chip type read response
			final byte[] response = helper
					.receiveBootLoaderReply(Messages.RAM_READ_RESPONSE);

			// Throw error if reading failed
			if (response[1] != 0x00) {
				log.error(String.format("Failed to read chip type from RAM: Response should be 0x00, yet it is: 0x%02x",
								response[1]));
				throw new RamReadFailedException();
			}

			chipType = determineChipType(response[2], response[3]);

			log.trace("Chip identified as " + chipType + " (received "
					+ StringUtils.toHexString(response[2]) + " "
					+ StringUtils.toHexString(response[3]) + ")");
		}
		return chipType;
	}

}
