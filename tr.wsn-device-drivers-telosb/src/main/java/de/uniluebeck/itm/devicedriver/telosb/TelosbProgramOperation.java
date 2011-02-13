package de.uniluebeck.itm.devicedriver.telosb;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.exception.FlashProgramFailedException;
import de.uniluebeck.itm.devicedriver.operation.AbstractProgramOperation;
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
	
	private void program(final Monitor monitor) throws Exception {
		final TelosbBinData binData = new TelosbBinData(getBinaryImage());
		// Write program to flash
		log.info("Starting to write program into flash memory...");
		
		BinDataBlock block;
		int blockCount = 0;
		int bytesProgrammed = 0;
		while ((block = binData.getNextBlock()) != null) {
			final byte[] data = block.getData();
			final int address = block.getAddress();
			
			// write single block
			try {
				final WriteFlashOperation writeFlashOperation = device.createWriteFlashOperation();
				writeFlashOperation.setData(address, data, data.length);
				executeSubOperation(writeFlashOperation, monitor);
			} catch (FlashProgramFailedException e) {
				log.error(String.format("Error writing %d bytes into flash " +
						"at address 0x%02x: " + e + ". Programmed " + bytesProgrammed + " bytes so far. "+
						". Operation will be canceled.", data.length, address), e);
				throw e;
			} catch (IOException e) {
				log.error("I/O error while writing flash: " +e+". Programmed "+bytesProgrammed+" bytes so far. " +
						"Operation will be canceled!", e);
				throw e;
			}
			
			bytesProgrammed += data.length;
			
			// Notify listeners of the new status
			final float progress = ((float) blockCount) / ((float) binData.getBlockCount());
			monitor.onProgressChange(progress);
			
			// Return if the user has requested to cancel this operation
			if (isCanceled()) {
				return;
			}
			
			blockCount++;
		}
		
		// reset device (exit boot loader)
		log.info("Resetting device.");
		executeSubOperation(device.createResetOperation(), monitor);
		
		log.debug("Programmed " + bytesProgrammed + " bytes.");
	}
	
	@Override
	public Void execute(final Monitor monitor) throws Exception {
		executeSubOperation(device.createEnterProgramModeOperation(), monitor);
		try {
			program(monitor);
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), monitor);
		}
		return null;
	}

}
