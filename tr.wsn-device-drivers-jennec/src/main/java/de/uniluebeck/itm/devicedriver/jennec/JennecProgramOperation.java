package de.uniluebeck.itm.devicedriver.jennec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.BinFileDataBlock;
import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.Sector;
import de.uniluebeck.itm.devicedriver.exception.ProgramChipMismatchException;
import de.uniluebeck.itm.devicedriver.operation.AbstractProgramOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteFlashOperation;

public class JennecProgramOperation extends AbstractProgramOperation {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennecProgramOperation.class);
	
	private final JennecDevice device;
	
	public JennecProgramOperation(JennecDevice device) {
		this.device = device;
	}
	
	public Void execute(Monitor monitor) throws Exception {
		JennicBinFile jennicProgram = (JennicBinFile) binaryImage;
		
		// Enter programming mode
		EnterProgramModeOperation enterProgramModeOperation = device.createEnterProgramModeOperation();
		if (!executeSubOperation(enterProgramModeOperation)) {
			return null;
		}

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

		// Check if file and current chip match
		if (!binaryImage.isCompatible(chipType)) {
			log.error("Chip type(" + chipType + ") and bin-program type(" + binaryImage.getFileType() + ") do not match");
			throw new ProgramChipMismatchException(chipType, binaryImage.getFileType());
		}
		
		// insert flash header of device
		try {
			final GetFlashHeaderOperation getFlashHeaderOperation = device.createGetFlashHeaderOperation();
			final byte[] flashHeader = executeSubOperation(getFlashHeaderOperation);
			if (!jennicProgram.insertHeader(flashHeader)) {
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
		BinFileDataBlock block = null;
		int blockCount = 0;
		while ((block = binaryImage.getNextBlock()) != null) {
			try {
				WriteFlashOperation operation = device.createWriteFlashOperation();
				operation.setData(block.address, block.data, block.data.length);
				executeSubOperation(operation);
			} catch (Exception e) {
				log.debug("Error while reading flash! Operation will be cancelled!");
				throw e;
			}
			
			// Notify listeners of the new status
			float progress = ((float) blockCount) / ((float) binaryImage.getBlockCount());
			monitor.onProgressChange(progress);
			
			// Return with success if the user has requested to cancel this
			// operation
			if (isCanceled()) {
				log.debug("Operation has been cancelled");
				return null;
			}
			
			blockCount++;
		}
		return null;
	}
}
