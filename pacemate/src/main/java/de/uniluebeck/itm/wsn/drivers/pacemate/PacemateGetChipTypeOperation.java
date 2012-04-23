package de.uniluebeck.itm.wsn.drivers.pacemate;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class PacemateGetChipTypeOperation extends TimeLimitedOperation<ChipType> implements GetChipTypeOperation {

	private static final Logger log = LoggerFactory.getLogger(PacemateGetChipTypeOperation.class);

	private final PacemateHelper helper;

	@Inject
	public PacemateGetChipTypeOperation(final TimeLimiter timeLimiter,
										final PacemateHelper helper,
										@Assisted final long timeoutMillis,
										@Assisted @Nullable final OperationListener<ChipType> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.helper = helper;
	}

	@Override
	@SerialPortProgrammingMode
	protected ChipType callInternal() throws Exception {

		// Send chip type read request
		helper.sendBootLoaderMessage(Messages.ReadPartIDRequestMessage());

		// Read chip type read response
		final String response = helper.receiveBootLoaderReplySuccess(Messages.CMD_SUCCESS);
		final ChipType chipType = ChipType.LPC2136;

		if (response.compareTo("196387") != 0) {
			throw new RuntimeException(
					"Defaulted to chip type LPC2136 (Pacemate). Identification may be wrong: " + response
			);
		}

		log.debug("Chip identified as " + chipType + " (received " + response + ")");
		return chipType;
	}
}
