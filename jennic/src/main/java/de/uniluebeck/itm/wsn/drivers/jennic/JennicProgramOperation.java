package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.exception.ProgramChipMismatchException;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.util.BinDataBlock;

public class JennicProgramOperation extends AbstractProgramOperation {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicProgramOperation.class);
	
	private final JennicSerialPortConnection device;
	
	private final Injector injector;
	
	@Inject
	public JennicProgramOperation(Injector injector) {
		this.device = injector.getInstance(JennicSerialPortConnection.class);
		this.injector = injector;
	}
	
	private void program(final ChipType chipType, final JennicBinData binData, final ProgressManager progressManager) throws Exception {
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
			progressManager.worked(1.0f / binData.getBlockCount());
			
			// Return with success if the user has requested to cancel this
			// operation
			if (isCanceled()) {
				return;
			}
			
			blockCount++;
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
	
	private void insertFlashHeaderToImage(JennicBinData binData, final ProgressManager progressManager) throws Exception {
		// insert flash header of device
		final byte[] flashHeader = executeSubOperation(injector.getInstance(GetFlashHeaderOperation.class), progressManager);
		binData.insertHeader(flashHeader);
	}
	
	public Void execute(final ProgressManager progressManager) throws Exception {
		final ChipType chipType = executeSubOperation(injector.getInstance(GetChipTypeOperation.class), progressManager.createSub(0.0625f));
		final JennicBinData binData = validateImage(chipType);
		insertFlashHeaderToImage(binData, progressManager.createSub(0.0625f));
		
		executeSubOperation(injector.getInstance(EnterProgramModeOperation.class), progressManager.createSub(0.0625f));
		try {
			program(chipType, binData, progressManager.createSub(0.75f));
		} finally {
			executeSubOperation(injector.getInstance(LeaveProgramModeOperation.class), progressManager.createSub(0.0125f));
			executeSubOperation(injector.getInstance(ResetOperation.class), progressManager.createSub(0.0500f));
		}
		return null;
	}
}
