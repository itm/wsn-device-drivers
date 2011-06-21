package de.uniluebeck.itm.wsn.drivers.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class PacemateGetChipTypeOperation extends AbstractOperation<ChipType> implements GetChipTypeOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateGetChipTypeOperation.class);
	
	private final PacemateSerialPortConnection connection;
	
	private final EnterProgramModeOperation enterProgramModeOperation;
	
	private final LeaveProgramModeOperation leaveProgramModeOperation;
	
	@Inject
	public PacemateGetChipTypeOperation(PacemateSerialPortConnection connection,
			EnterProgramModeOperation enterProgramModeOperation,
			LeaveProgramModeOperation leaveProgramModeOperation) {
		this.connection = connection;
		this.enterProgramModeOperation = enterProgramModeOperation;
		this.leaveProgramModeOperation = leaveProgramModeOperation;
	}
	
	private ChipType getChipType(final ProgressManager progressManager) throws Exception {
		connection.clearStreamData();
		connection.autobaud();

		connection.waitForBootLoader();

		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			return null;
		}
		
		// Send chip type read request
		connection.sendBootLoaderMessage(Messages.ReadPartIDRequestMessage());

		// Read chip type read response
		final String response = connection.receiveBootLoaderReplySuccess(Messages.CMD_SUCCESS);
		final ChipType chipType = ChipType.LPC2136;

		if (response.compareTo("196387") != 0) {
			log.error("Defaulted to chip type LPC2136 (Pacemate). Identification may be wrong." + response);
		}

		log.debug("Chip identified as " + chipType + " (received " + response + ")");
		progressManager.done();
		return chipType;
	}
	
	@Override
	public ChipType execute(final ProgressManager progressManager) throws Exception {
		executeSubOperation(enterProgramModeOperation, progressManager.createSub(0.25f));
		ChipType chipType = ChipType.UNKNOWN;
		try {
			chipType = getChipType(progressManager.createSub(0.25f));
		} finally {
			executeSubOperation(leaveProgramModeOperation, progressManager.createSub(0.5f));
		}
		return chipType;
	}

}
