package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractWriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class JennicWriteFlashOperation extends AbstractWriteFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicWriteFlashOperation.class);
	
	private final Injector injector;
	
	@Inject
	public JennicWriteFlashOperation(Injector injector) {
		this.injector = injector;
	}
	
	@Override
	public Void execute(final ProgressManager monitor) throws Exception {
		log.trace("Writing to flash...");
		executeSubOperation(injector.getInstance(EnterProgramModeOperation.class), monitor.createSub(0.5f));
		try {
			injector.getInstance(JennicSerialPortConnection.class).writeFlash(getAddress(), getData());
		} finally {
			executeSubOperation(injector.getInstance(LeaveProgramModeOperation.class), monitor.createSub(0.5f));
		}
		log.trace("Flash written");
		return null;
	}

	
}
