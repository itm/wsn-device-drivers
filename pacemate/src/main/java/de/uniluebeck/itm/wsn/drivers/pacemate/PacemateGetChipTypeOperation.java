package de.uniluebeck.itm.wsn.drivers.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.serialport.Program;

public class PacemateGetChipTypeOperation implements GetChipTypeOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateGetChipTypeOperation.class);
	
	private final PacemateHelper helper;
	
	@Inject
	public PacemateGetChipTypeOperation(PacemateHelper helper) {
		this.helper = helper;
	}
	
	@Override
	@Program
	public ChipType run(ProgressManager progressManager, OperationContext context) throws Exception {
		helper.clearStreamData();
		helper.autobaud();
	
		helper.waitForBootLoader();
	
		// Return with success if the user has requested to cancel this
		// operation
		if (context.isCanceled()) {
			return null;
		}
		
		// Send chip type read request
		helper.sendBootLoaderMessage(Messages.ReadPartIDRequestMessage());
	
		// Read chip type read response
		final String response = helper.receiveBootLoaderReplySuccess(Messages.CMD_SUCCESS);
		final ChipType chipType = ChipType.LPC2136;
	
		if (response.compareTo("196387") != 0) {
			log.error("Defaulted to chip type LPC2136 (Pacemate). Identification may be wrong." + response);
		}
	
		log.debug("Chip identified as " + chipType + " (received " + response + ")");
		progressManager.done();
		return chipType;
	}

}
