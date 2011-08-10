package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.exception.ProgramChipMismatchException;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.util.BinDataBlock;

public class JennicProgramOperation extends AbstractProgramOperation {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicProgramOperation.class);
	
	private final JennicHelper helper;
	
	private final GetFlashHeaderOperation getFlashHeaderOperation;
	
	private final GetChipTypeOperation getChipTypeOperation;
	
	private final EnterProgramModeOperation enterProgramModeOperation;
	
	private final LeaveProgramModeOperation leaveProgramModeOperation;
	
	private final ResetOperation resetOperation;
	
	@Inject
	public JennicProgramOperation(JennicHelper helper, 
			GetFlashHeaderOperation getFlashHeaderOperation,
			GetChipTypeOperation getChipTypeOperation,
			EnterProgramModeOperation enterProgramModeOperation,
			LeaveProgramModeOperation leaveProgramModeOperation,
			ResetOperation resetOperation) {
		this.helper = helper;
		this.getFlashHeaderOperation = getFlashHeaderOperation;
		this.getChipTypeOperation = getChipTypeOperation;
		this.enterProgramModeOperation = enterProgramModeOperation;
		this.leaveProgramModeOperation = leaveProgramModeOperation;
		this.resetOperation = resetOperation;
	}
	
	private void program(ChipType chipType, JennicBinData binData, ProgressManager progressManager, OperationContext context) throws Exception {
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
	
	private void insertFlashHeaderToImage(JennicBinData binData, final ProgressManager progressManager, OperationContext context) throws Exception {
		// insert flash header of helper
		final byte[] flashHeader = context.run(getFlashHeaderOperation, progressManager);
		binData.insertHeader(flashHeader);
	}
	
	public Void run(final ProgressManager progressManager, OperationContext context) throws Exception {
		final ChipType chipType = context.run(getChipTypeOperation, progressManager, 0.0625f);
		final JennicBinData binData = validateImage(chipType);
		insertFlashHeaderToImage(binData, progressManager.createSub(0.0625f), context);
		
		context.run(enterProgramModeOperation, progressManager, 0.0625f);
		try {
			program(chipType, binData, progressManager.createSub(0.75f), context);
		} finally {
			context.run(leaveProgramModeOperation, progressManager, 0.0125f);
			context.run(resetOperation, progressManager, 0.05f);
		}
		return null;
	}
}
