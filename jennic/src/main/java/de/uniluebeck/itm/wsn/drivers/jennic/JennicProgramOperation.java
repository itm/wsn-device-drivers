package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.exception.FlashProgramFailedException;
import de.uniluebeck.itm.wsn.drivers.core.exception.ProgramChipMismatchException;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.Program;
import de.uniluebeck.itm.wsn.drivers.core.util.BinDataBlock;

public class JennicProgramOperation extends AbstractProgramOperation {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicProgramOperation.class);
	
	private static final int MAX_RETRIES = 3;
	
	private final JennicHelper helper;
	
	private final GetChipTypeOperation getChipTypeOperation;
	
	private final Provider<ReadFlashOperation> readFlashOperationProvider;
	
	private final ResetOperation resetOperation;
	
	@Inject
	public JennicProgramOperation(JennicHelper helper, 
			GetChipTypeOperation getChipTypeOperation,
			Provider<ReadFlashOperation> readFlashOperationProvider,
			ResetOperation resetOperation) {
		this.helper = helper;
		this.getChipTypeOperation = getChipTypeOperation;
		this.readFlashOperationProvider = readFlashOperationProvider;
		this.resetOperation = resetOperation;
	}
	
	@Program
	void program(ProgressManager progressManager, OperationContext context) throws Exception {
		ChipType chipType = context.run(getChipTypeOperation, progressManager, 0.0625f);
		JennicBinData binData = validateImage(chipType);
		insertFlashHeaderToImage(chipType, binData, progressManager.createSub(0.0625f), context);
		
		// Wait for a connection
		while (!context.isCanceled() && !helper.waitForConnection()) {
			log.debug("Still waiting for a connection");
		}

		// Return with success if the user has requested to cancel this
		// operation
		if (context.isCanceled()) {
			return;
		}		
		
		helper.configureFlash(chipType);
		helper.eraseFlash(Sector.FIRST);
		helper.eraseFlash(Sector.SECOND);
		helper.eraseFlash(Sector.THIRD);
		
		// Write program to flash
		BinDataBlock block = null;
		while ((block = binData.getNextBlock()) != null) {
			helper.writeFlash(block.getAddress(), block.getData());
			
			// Notify listeners of the new status
			progressManager.worked(1.0f / binData.getBlockCount());
			
			// Return with success if the user has requested to cancel this
			// operation
			if (context.isCanceled()) {
				return;
			}
		}	
	}
	
	private JennicBinData validateImage(final ChipType chipType) throws Exception {
		final JennicBinData binData = new JennicBinData(getBinaryImage());
		// Check if file and current chip match
		if (!binData.isCompatible(chipType)) {
			log.error("Chip type(" + chipType + ") and bin-program type(" + binData.getChipType() + ") do not match");
			throw new ProgramChipMismatchException(chipType, binData.getChipType());
		}
		return binData;
	}
	
	private void insertFlashHeaderToImage(ChipType chipType, JennicBinData binData, ProgressManager progressManager, OperationContext context) throws Exception {
		final int address = chipType.getHeaderStart();
		final int length = chipType.getHeaderLength();
		for (int i = 0; i < MAX_RETRIES; ++i) {
			ReadFlashOperation operation = readFlashOperationProvider.get();
			operation.setAddress(address, length);
			byte[] flashHeader = context.run(operation, progressManager, 0.33f);
			if (validateFlashHeader(flashHeader)) {
				binData.insertHeader(flashHeader);
				progressManager.done();
				return;
			}
			Thread.sleep(1000);
		}
		throw new FlashProgramFailedException("Unable to save mac address before flashing");
	}
	
	/**
	 * Checks if the mac address is not 0xFF...FF
	 * 
	 * @param header The flash header.
	 * @return true if not else false
	 */
	private boolean validateFlashHeader(byte[] header) {
		MacAddress macAddress = new MacAddress(header);
		return MacAddress.HIGHEST_MAC_ADDRESS.equals(macAddress) == false;
	}
	
	public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
		program(progressManager.createSub(0.95f), context);
		context.run(resetOperation, progressManager, 0.05f);
		return null;
	}
}
