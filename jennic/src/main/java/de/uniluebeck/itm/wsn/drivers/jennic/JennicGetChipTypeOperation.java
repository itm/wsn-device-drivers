package de.uniluebeck.itm.wsn.drivers.jennic;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.exception.RamReadFailedException;
import de.uniluebeck.itm.wsn.drivers.core.exception.UnexpectedResponseException;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import static de.uniluebeck.itm.util.StringUtils.toHexString;

public class JennicGetChipTypeOperation extends TimeLimitedOperation<ChipType> implements GetChipTypeOperation {

	private static final Logger log = LoggerFactory.getLogger(JennicGetChipTypeOperation.class);

	private final JennicHelper helper;

	@Inject
	public JennicGetChipTypeOperation(final TimeLimiter timeLimiter,
									  final JennicHelper helper,
									  @Assisted final long timeoutMillis,
									  @Assisted @Nullable final OperationListener<ChipType> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.helper = helper;
	}

	private ChipType determineChipType(byte s, byte t) {

		if (s == 0x00 && t == 0x20) {
			return ChipType.JN513X;
		} else if (s == 0x10 && t == 0x00) {
			return ChipType.JN513XR1;
		} else if (s == 0x20 && t == 0x00) {
			return ChipType.JN5121;
		} else {
			log.warn("Defaulted to chip type JN5121. Identification may be wrong.");
			return ChipType.UNKNOWN;
		}
	}

	@Override
	@SerialPortProgrammingMode
	protected ChipType callInternal() throws Exception {

		log.trace("Getting ChipType...");

		ChipType chipType = ChipType.UNKNOWN;

		helper.sendBootloaderMessage(Messages.chipIdMessage());

		try {

			// read chip type read response
			byte[] res = helper.receiveBootloaderReply(Messages.CHIP_ID_RESPONSE);

			if (log.isTraceEnabled()) {
				String S = "received: (len=" + res.length + ") ";
				for (int i = 0; i < res.length; i++) {
					S = S + "res[" + i + "]=" + StringUtils.toHexString(res[i]) + " ";
				}
				log.trace(S);
			}

			if (res.length == 6) {
				if (((res[1] == 0) && (res[2] == 0x10) && (res[3] == 0x40)
						&& (res[4] == 0x46) && (res[5] == (byte) 0x86))
						|| ((res[1] == 0) && (res[2] == 0x10)
						&& (res[3] == (byte) 0x80) && (res[4] == 0x46) && (res[5] == (byte) 0x86))) {
					chipType = ChipType.JN5148;
					log.debug("Chip identified as " + chipType + ".");
				} else {
					log.error("BYTES incorrect res[1]=" + res[1] + " != " + 0
							+ "res[2]=" + res[2] + " != " + 0x10 + "res[3]="
							+ res[3] + " != " + 0x40 + "res[4]=" + res[4]
							+ " != " + 0x46 + "res[5]=" + res[5] + " != "
							+ (byte) 0x86
					);
				}
			} else {
				log.error("length incorrect");
			}
		} catch (UnexpectedResponseException e) {

			// send chip type read request
			helper.sendBootloaderMessage(Messages.ramReadRequestMessage(0x100000FC, 0x0004));

			// read chip type read response
			final byte[] response = helper.receiveBootloaderReply(Messages.RAM_READ_RESPONSE);

			// Throw error if reading failed
			if (response[1] != 0x00) {

				if (log.isErrorEnabled()) {
					log.error("Failed to read chip type: should be 0x00, yet it is: ", toHexString(response[1]));
				}

				throw new RamReadFailedException();
			}

			chipType = determineChipType(response[2], response[3]);

			log.trace("Chip identified as {} (received {} {})",
					new Object[]{chipType, toHexString(response[2]), toHexString(response[3])}
			);
		}
		return chipType;
	}
}
