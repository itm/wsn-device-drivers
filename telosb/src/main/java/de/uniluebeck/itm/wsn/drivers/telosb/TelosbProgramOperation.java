package de.uniluebeck.itm.wsn.drivers.telosb;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.wsn.drivers.core.exception.FlashProgramFailedException;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.util.BinDataBlock;

public class TelosbProgramOperation extends AbstractProgramOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(TelosbProgramOperation.class);
	
	private final TelosbDevice device;
	
	public TelosbProgramOperation(TelosbDevice device) {
		this.device = device;
	}
	
	private void program(final ProgressManager progressManager) throws Exception {
		final TelosbBinData binData = new TelosbBinData(getBinaryImage());
		// Write program to flash
		log.trace("Starting to write program into flash memory...");
		
		final float worked = 1.0f / binData.getBlockCount();
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
			} catch (final IOException e) {
				log.error("I/O error while writing flash. Programmed " + bytesProgrammed + " bytes so far.", e);
				throw e;
			}
			
			bytesProgrammed += data.length;
			
			// Notify listeners of the new status
			progressManager.worked(worked);
			
			// Return if the user has requested to cancel this operation
			if (isCanceled()) {
				return;
			}
			
			blockCount++;
		}		
		log.trace("Programmed " + bytesProgrammed + " bytes.");
	}
	
	@Override
	public Void execute(final ProgressManager progressManager) throws Exception {
		executeSubOperation(device.createEnterProgramModeOperation(), progressManager.createSub(0.125f));
		try {
			program(progressManager.createSub(0.75f));
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), progressManager.createSub(0.0625f));
		}
		executeSubOperation(device.createResetOperation(), progressManager.createSub(0.0625f));
		return null;
	}

}
