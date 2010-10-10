package de.uniluebeck.itm.devicedriver.telosb;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.DeviceBinDataBlock;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.exception.FlashProgramFailedException;
import de.uniluebeck.itm.devicedriver.exception.ProgramChipMismatchException;
import de.uniluebeck.itm.devicedriver.operation.AbstractProgramOperation;
import de.uniluebeck.itm.devicedriver.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.devicedriver.operation.ResetOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteFlashOperation;

public class TelosbProgramOperation extends AbstractProgramOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(TelosbProgramOperation.class);
	
	private final TelosbDevice device;
	
	public TelosbProgramOperation(TelosbDevice device) {
		this.device = device;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {		
		// enter programming mode
		final EnterProgramModeOperation enterProgramModeOperation = device.createEnterProgramModeOperation();
		executeSubOperation(enterProgramModeOperation);
		
		// Check if file and current chip match
		final GetChipTypeOperation getChipTypeOperation = device.createGetChipTypeOperation();
		final ChipType chipType = executeSubOperation(getChipTypeOperation);
		if ( !binaryImage.isCompatible(chipType) ) {
			log.error("Chip type(" + chipType + ") and bin-program type(" + binaryImage.getChipType() + ") do not match");
			throw new ProgramChipMismatchException(chipType, binaryImage.getChipType());
		}
		
		// Write program to flash
		log.info("Starting to write program into flash memory...");
		
		DeviceBinDataBlock block;
		int blockCount = 0;
		int bytesProgrammed = 0;
		while ((block = binaryImage.getNextBlock()) != null) {
			
			// write single block
			try {
				WriteFlashOperation writeFlashOperation = device.createWriteFlashOperation();
				writeFlashOperation.setData(block.address, block.data, block.data.length);
				executeSubOperation(writeFlashOperation);
			} catch (FlashProgramFailedException e) {
				log.error(String.format("Error writing %d bytes into flash " +
						"at address 0x%02x: " + e + ". Programmed " + bytesProgrammed + " bytes so far. "+
						". Operation will be canceled.", block.data.length, block.address), e);
				throw e;
			} catch (IOException e) {
				log.error("I/O error while writing flash: " +e+". Programmed "+bytesProgrammed+" bytes so far. " +
						"Operation will be canceled!", e);
				throw e;
			}
			
			bytesProgrammed += block.data.length;
			
			// Notify listeners of the new status
			float progress = ((float) blockCount) / ((float) binaryImage.getBlockCount());
			monitor.onProgressChange(progress);
			
			// Return if the user has requested to cancel this operation
			if (isCanceled()) {
				return null;
			}
			
			blockCount++;
		}
		
		// reset device (exit boot loader)
		log.info("Resetting device.");
		ResetOperation resetOperation = device.createResetOperation();
		executeSubOperation(resetOperation);
		
		log.debug("Programmed " + bytesProgrammed + " bytes.");
		
		LeaveProgramModeOperation leaveProgramModeOperation = device.createLeaveProgramModeOperation();
		executeSubOperation(leaveProgramModeOperation);
		return null;
	}

}
