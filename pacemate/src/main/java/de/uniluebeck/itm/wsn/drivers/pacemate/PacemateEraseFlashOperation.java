package de.uniluebeck.itm.wsn.drivers.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.serialport.Program;

public class PacemateEraseFlashOperation implements EraseFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateEraseFlashOperation.class);
	
	private static final int START_ADDRESS = 3;
	
	private static final int END_ADDRESS = 14;
	
	private final PacemateHelper helper;
	
	@Inject
	public PacemateEraseFlashOperation(PacemateHelper helper) {
		this.helper = helper;
	}

	@Override
	@Program
	public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
		log.debug("Erasing whole flash...");
		helper.clearStreamData();
		helper.autobaud();

		helper.waitForBootLoader();

		// Return with success if the user has requested to cancel this
		// operation
		if (context.isCanceled()) {
			return null;
		}
		
		helper.configureFlash(START_ADDRESS, END_ADDRESS);
		progressManager.worked(0.25f);
		helper.eraseFlash(START_ADDRESS, END_ADDRESS);
		log.debug("Flash completly erased");
		return null;
	}
}
