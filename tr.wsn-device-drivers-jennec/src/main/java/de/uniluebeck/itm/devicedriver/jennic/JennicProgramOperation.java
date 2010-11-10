package de.uniluebeck.itm.devicedriver.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.exception.ProgramChipMismatchException;
import de.uniluebeck.itm.devicedriver.operation.AbstractProgramOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteFlashOperation;
import de.uniluebeck.itm.devicedriver.util.BinDataBlock;

public class JennicProgramOperation extends AbstractProgramOperation {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicProgramOperation.class);
	
	private final JennicDevice device;
	
	public JennicProgramOperation(JennicDevice device) {
		this.device = device;
	}
	
	public Void execute(Monitor monitor) throws Exception {
		// Enter programming mode
		executeSubOperation(device.createEnterProgramModeOperation());

		// Wait for a connection
		while (!isCanceled() && !device.waitForConnection()) {
			log.info("Still waiting for a connection");
		}

		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			log.debug("Operation has been cancelled");
			return null;
		}

		// Connection established, determine chip type
		final ChipType chipType = executeSubOperation(device.createGetChipTypeOperation());
		//log.debug("Chip type is " + chipType);

		final JennicBinData binData = new JennicBinData(binaryImage);
		// Check if file and current chip match
		if (!binData.isCompatible(chipType)) {
			log.error("Chip type(" + chipType + ") and bin-program type(" + binData.getChipType() + ") do not match");
			throw new ProgramChipMismatchException(chipType, binData.getChipType());
		}
		
		// insert flash header of device
		try {
			final GetFlashHeaderOperation getFlashHeaderOperation = device.createGetFlashHeaderOperation();
			final byte[] flashHeader = executeSubOperation(getFlashHeaderOperation);
			if (!binData.insertHeader(flashHeader)) {
				log.error("Unable to write flash header to binary file.");
				return null;
			}
		} catch (ClassCastException e) {
			log.error("Supplied binary file for programming the jennic device was not a jennic file. Unable to insert flash header.");
			return null;
		}

		device.configureFlash(chipType);
		device.eraseFlash(Sector.FIRST);
		device.eraseFlash(Sector.SECOND);
		device.eraseFlash(Sector.THIRD);
		
		// Write program to flash
		BinDataBlock block = null;
		int blockCount = 0;
		while ((block = binData.getNextBlock()) != null) {
			try {
				WriteFlashOperation operation = device.createWriteFlashOperation();
				operation.setData(block.address, block.data, block.data.length);
				executeSubOperation(operation);
			} catch (Exception e) {
				log.debug("Error while reading flash! Operation will be cancelled!");
				throw e;
			}
			
			// Notify listeners of the new status
			float progress = ((float) blockCount) / ((float) binData.getBlockCount());
			monitor.onProgressChange(progress);
			
			// Return with success if the user has requested to cancel this
			// operation
			if (isCanceled()) {
				log.debug("Operation has been cancelled");
				return null;
			}
			
			blockCount++;
		}
		
		executeSubOperation(device.createLeaveProgramModeOperation());
		
		return null;
	}
}
