package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.serialport.Program;


public class JennicReadFlashOperation extends AbstractReadFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicReadFlashOperation.class);
	
	private final JennicHelper helper;
	
	@Inject
	public JennicReadFlashOperation(JennicHelper helper) {
		this.helper = helper;
	}
	
	private byte[] readFlash(ProgressManager progressManager, OperationContext context) throws Exception {
		// Wait for a connection
		while (!context.isCanceled() && !helper.waitForConnection()) {
			log.debug("Still waiting for a connection");
		}

		// Return with success if the user has requested to cancel this
		// operation
		if (context.isCanceled()) {
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
	@Program
	public byte[] run(ProgressManager progressManager, OperationContext context) throws Exception {
		return readFlash(progressManager, context);
	}
}
