package de.uniluebeck.itm.rsc.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.rsc.drivers.core.ChipType;
import de.uniluebeck.itm.rsc.drivers.core.Monitor;
import de.uniluebeck.itm.rsc.drivers.core.exception.ProgramChipMismatchException;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractProgramOperation;
import de.uniluebeck.itm.rsc.drivers.core.util.BinDataBlock;

public class JennicProgramOperation extends AbstractProgramOperation {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicProgramOperation.class);
	
	private final JennicDevice device;
	
	public JennicProgramOperation(JennicDevice device) {
		this.device = device;
	}
	
	private void program(final ChipType chipType, final JennicBinData binData, final Monitor monitor) throws Exception {
		// Wait for a connection
		while (!isCanceled() && !device.waitForConnection()) {
			log.debug("Still waiting for a connection");
		}

		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			return;
		}		
		
		device.configureFlash(chipType);
		device.eraseFlash(Sector.FIRST);
		device.eraseFlash(Sector.SECOND);
		device.eraseFlash(Sector.THIRD);
		
		// Write program to flash
		BinDataBlock block = null;
		int blockCount = 0;
		while ((block = binData.getNextBlock()) != null) {
			device.writeFlash(block.getAddress(), block.getData());
			
			// Notify listeners of the new status
			float progress = ((float) blockCount) / ((float) binData.getBlockCount());
			monitor.onProgressChange(progress);
			
			// Return with success if the user has requested to cancel this
			// operation
			if (isCanceled()) {
				return;
			}
			
			blockCount++;
		}	
	}
	
	private JennicBinData validateImage(final ChipType chipType, final Monitor monitor) throws Exception {
		final JennicBinData binData = new JennicBinData(getBinaryImage());
		// Check if file and current chip match
		if (!binData.isCompatible(chipType)) {
			log.error("Chip type(" + chipType + ") and bin-program type(" + binData.getChipType() + ") do not match");
			throw new ProgramChipMismatchException(chipType, binData.getChipType());
		}
		return binData;
	}
	
	private void insertFlashHeaderToImage(JennicBinData binData, final Monitor monitor) throws Exception {
		// insert flash header of device
		final byte[] flashHeader = executeSubOperation(device.createGetFlashHeaderOperation(), monitor);
		binData.insertHeader(flashHeader);
	}
	
	public Void execute(Monitor monitor) throws Exception {
		final ChipType chipType = executeSubOperation(device.createGetChipTypeOperation(), monitor);
		final JennicBinData binData = validateImage(chipType, monitor);
		insertFlashHeaderToImage(binData, monitor);
		
		executeSubOperation(device.createEnterProgramModeOperation(), monitor);
		try {
			program(chipType, binData, monitor);
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), monitor);
		}
		return null;
	}
}
