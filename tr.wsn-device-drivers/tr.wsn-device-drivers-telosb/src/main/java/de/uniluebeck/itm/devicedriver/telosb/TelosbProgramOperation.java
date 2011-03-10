package de.uniluebeck.itm.devicedriver.telosb;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.exception.FlashProgramFailedException;
import de.uniluebeck.itm.devicedriver.operation.AbstractProgramOperation;
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
		log.debug("Starting to write program into flash memory...");
		
		int blockCount = 0;
		int bytesProgrammed = 0;
		for (BinDataBlock block = binData.getNextBlock(); block != null; block = binData.getNextBlock()) {
			final byte[] data = block.getData();
			final int address = block.getAddress();
			
			// write single block
			try {
				device.writeFlash(address, data, data.length);
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
			final float progress = ((float) blockCount) / binData.getBlockCount();
			monitor.onProgressChange(progress);
			
			// Return if the user has requested to cancel this operation
			if (isCanceled()) {
				return;
			}
			
			blockCount++;
		}		
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
		executeSubOperation(device.createResetOperation(), monitor);
		return null;
	}

}
