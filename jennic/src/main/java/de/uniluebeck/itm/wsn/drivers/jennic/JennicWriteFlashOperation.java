package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractWriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class JennicWriteFlashOperation extends AbstractWriteFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicWriteFlashOperation.class);
	
	private final JennicHelper helper;
	
	private final EnterProgramModeOperation enterProgramModeOperation;
	
	private final LeaveProgramModeOperation leaveProgramModeOperation;
	
	@Inject
	public JennicWriteFlashOperation(JennicHelper helper,
			EnterProgramModeOperation enterProgramModeOperation,
			LeaveProgramModeOperation leaveProgramModeOperation) {
		this.helper = helper;
		this.enterProgramModeOperation = enterProgramModeOperation;
		this.leaveProgramModeOperation = leaveProgramModeOperation;
	}
	
	@Override
	public Void execute(final ProgressManager monitor) throws Exception {
		log.trace("Writing to flash...");
		executeSubOperation(enterProgramModeOperation, monitor.createSub(0.5f));
		try {
			helper.writeFlash(getAddress(), getData());
		} finally {
			executeSubOperation(leaveProgramModeOperation, monitor.createSub(0.5f));
		}
		log.trace("Flash written");
		return null;
	}

	
}
