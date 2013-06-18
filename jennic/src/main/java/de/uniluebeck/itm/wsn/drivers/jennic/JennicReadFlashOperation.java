package de.uniluebeck.itm.wsn.drivers.jennic;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class JennicReadFlashOperation extends AbstractReadFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicReadFlashOperation.class);

	private final JennicHelper helper;

	@Inject
	public JennicReadFlashOperation(final TimeLimiter timeLimiter,
									final JennicHelper helper,
									@Assisted("address") int address,
									@Assisted("length") int length,
									@Assisted final long timeoutMillis,
									@Assisted @Nullable final OperationListener<byte[]> operationCallback) {
		super(timeLimiter, address, length, timeoutMillis, operationCallback);
		this.helper = helper;
	}

	@Override
	@SerialPortProgrammingMode
	protected byte[] callInternal() throws Exception {

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
		final float worked = 32.0f / (length <= 32.0f ? 32.0f : length);
		int sectorStart = address;

		while (sectorStart < sectorEnd) {

			// Determine length of the data block to read
			final int blockSize = sectorStart + 32 > sectorEnd ? length : 32;

			// Read data block
			byte[] data = helper.readFlash(sectorStart, blockSize);
			System.arraycopy(data, 0, flashData, sectorStart - address, data.length);

			// Notify listeners
			progress(worked);

			// Increment start address
			sectorStart += blockSize;

		}
		log.trace("Done, result is: " + StringUtils.toHexString(flashData));
		return flashData;
	}
}
