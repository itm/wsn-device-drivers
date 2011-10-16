package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractWriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.serialport.Program;

public class JennicWriteFlashOperation extends AbstractWriteFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicWriteFlashOperation.class);
	
	private final JennicHelper helper;
	
	@Inject
	public JennicWriteFlashOperation(JennicHelper helper) {
		this.helper = helper;
	}
	
	@Override
	@Program
	public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
		log.trace("Writing to flash...");
		helper.writeFlash(getAddress(), getData());
		log.trace("Flash written");
		return null;
	}

	
}
