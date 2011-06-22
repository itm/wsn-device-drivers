package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class JennicReadFlashOperation extends AbstractReadFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicReadFlashOperation.class);
	
	private final JennicHelper helper;
	
	private final EnterProgramModeOperation enterProgramModeOperation;
	
	private final LeaveProgramModeOperation leaveProgramModeOperation;
	
	@Inject
	public JennicReadFlashOperation(JennicHelper helper,
			EnterProgramModeOperation enterProgramModeOperation,
			LeaveProgramModeOperation leaveProgramModeOperation) {
		this.helper = helper;
		this.enterProgramModeOperation = enterProgramModeOperation;
		this.leaveProgramModeOperation = leaveProgramModeOperation;
	}
	
	private byte[] readFlash(final ProgressManager progressManager) throws Exception {
		// Wait for a connection
		while (!isCanceled() && !helper.waitForConnection()) {
			log.debug("Still waiting for a connection");
		}

		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			return null;
		}

		// Read all sectors
		final int address = getAddress();
		final int length = getLength();
		final byte flashData[] = new byte[length];
		final int sectorEnd = address + length;
		final float worked = 32.0f / length;
		int sectorStart = address;

		while (sectorStart < sectorEnd) {
			// Determine length of the data block to read
			final int blockSize = sectorStart + 32 > sectorEnd ? length : 32;

			// Read data block
			byte[] data = helper.readFlash(sectorStart, blockSize);
			System.arraycopy(data, 0, flashData, sectorStart - address, data.length);
			
			// Notify listeners
			progressManager.worked(worked);

			// Increment start address
			sectorStart += blockSize;

		}
		log.trace("Done, result is: " + StringUtils.toHexString(flashData));
		return flashData;
	}
	
	@Override
	public byte[] execute(final ProgressManager progressManager) throws Exception {
		byte[] data = null;
		// Enter programming mode
		executeSubOperation(enterProgramModeOperation, progressManager.createSub(0.125f));
		try {
			data = readFlash(progressManager.createSub(0.75f));
		} finally {
			executeSubOperation(leaveProgramModeOperation, progressManager.createSub(0.125f));
		}
		return data;
	}
	


}
