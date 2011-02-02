package de.uniluebeck.itm.devicedriver.telosb;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.exception.FlashProgramFailedException;
import de.uniluebeck.itm.devicedriver.exception.ProgramChipMismatchException;
import de.uniluebeck.itm.devicedriver.operation.AbstractProgramOperation;
import de.uniluebeck.itm.devicedriver.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteFlashOperation;
import de.uniluebeck.itm.devicedriver.util.BinDataBlock;

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
	public Void execute(final Monitor monitor) throws Exception {		
		// enter programming mode
		final EnterProgramModeOperation enterProgramModeOperation = device.createEnterProgramModeOperation();
		executeSubOperation(enterProgramModeOperation, monitor);
		
		// Check if file and current chip match
		final GetChipTypeOperation getChipTypeOperation = device.createGetChipTypeOperation();
		final ChipType chipType = executeSubOperation(getChipTypeOperation, monitor);
		final TelosbBinData binData = new TelosbBinData(binaryImage);
		if ( !binData.isCompatible(chipType) ) {
			log.error("Chip type(" + chipType + ") and bin-program type(" + binData.getChipType() + ") do not match");
			throw new ProgramChipMismatchException(chipType, binData.getChipType());
		}
		
		// Write program to flash
		log.info("Starting to write program into flash memory...");
		
		BinDataBlock block;
		int blockCount = 0;
		int bytesProgrammed = 0;
		while ((block = binData.getNextBlock()) != null) {
			
			// write single block
			try {
				WriteFlashOperation writeFlashOperation = device.createWriteFlashOperation();
				writeFlashOperation.setData(block.address, block.data, block.data.length);
				executeSubOperation(writeFlashOperation, monitor);
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
			float progress = ((float) blockCount) / ((float) binData.getBlockCount());
			monitor.onProgressChange(progress);
			
			// Return if the user has requested to cancel this operation
			if (isCanceled()) {
				return null;
			}
			
			blockCount++;
		}
		
		// reset device (exit boot loader)
		log.info("Resetting device.");
		executeSubOperation(device.createResetOperation(), monitor);
		
		log.debug("Programmed " + bytesProgrammed + " bytes.");
		
		executeSubOperation(device.createLeaveProgramModeOperation(), monitor);
		return null;
	}

}
