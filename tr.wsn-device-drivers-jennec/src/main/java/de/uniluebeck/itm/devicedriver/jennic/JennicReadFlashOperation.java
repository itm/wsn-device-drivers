package de.uniluebeck.itm.devicedriver.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractReadFlashOperation;
import de.uniluebeck.itm.tr.util.StringUtils;

public class JennicReadFlashOperation extends AbstractReadFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicReadFlashOperation.class);
	
	private final JennicDevice device;
	
	public JennicReadFlashOperation(JennicDevice device) {
		this.device = device;
	}
	
	@Override
	public byte[] execute(Monitor monitor) throws Exception {
		// Enter programming mode
		executeSubOperation(device.createEnterProgramModeOperation(), monitor);

		// Wait for a connection
		while (!isCanceled() && !device.waitForConnection()) {
			log.info("Still waiting for a connection");
		}

		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			return null;
		}

		// Read all sectors
		byte flashData[] = new byte[length];
		try {
			int sectorStart = address;
			int sectorEnd = address + length;

			while (sectorStart < sectorEnd) {
				// Determine length of the data block to read
				final int blockSize = sectorStart + 32 > sectorEnd ? length : 32;

				// Read data block
				byte[] data = readFlash(sectorStart, blockSize);
				System.arraycopy(data, 0, flashData, sectorStart - address, data.length);
				
				// Notify listeners
				float progress = ((float) (sectorStart - address)) / length;
				monitor.onProgressChange(progress);

				// Increment start address
				sectorStart += blockSize;

			}
			log.debug("Done, result is: " + StringUtils.toHexString(flashData));
		} catch (Exception e) {
			log.error("Error while reading flash contents: " + e, e);
			throw e;
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), monitor);
		}
		return flashData;
	}
	
	public byte[] readFlash(int address, int len) throws Exception {
		// Send flash program request
		device.sendBootLoaderMessage(Messages.flashReadRequestMessage(address, len));

		// Read flash program response
		byte[] response = device.receiveBootLoaderReply(Messages.FLASH_READ_RESPONSE);

		// Remove type and success octet
		byte[] data = new byte[response.length - 2];
		System.arraycopy(response, 2, data, 0, response.length - 2);

		// Return data
		return data;
	}

}
