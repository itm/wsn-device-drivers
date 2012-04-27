package de.uniluebeck.itm.wsn.drivers.pacemate;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.exception.InvalidChecksumException;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationFactory;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import de.uniluebeck.itm.wsn.drivers.core.util.BinaryImageBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class PacemateProgramOperation extends AbstractProgramOperation {

	private static final Logger log = LoggerFactory.getLogger(PacemateProgramOperation.class);

	private final PacemateHelper helper;

	private final OperationFactory operationFactory;

	@Inject
	public PacemateProgramOperation(final TimeLimiter timeLimiter,
									final PacemateHelper helper,
									final OperationFactory operationFactory,
									@Assisted byte[] binaryImage,
									@Assisted final long timeoutMillis,
									@Assisted @Nullable final OperationListener<Void> operationCallback) {
		super(timeLimiter, binaryImage, timeoutMillis, operationCallback);
		this.helper = helper;
		this.operationFactory = operationFactory;
	}

	private static final float PROGRESS_FRACTION_PROGRAM = 0.875f;

	private void program() throws Exception {

		// Return with success if the user has requested to cancel this operation
		if (isCanceled()) {
			return;
		}

		// Create pacemate image
		final PacemateBinaryImage binaryImage = new PacemateBinaryImage(getBinaryImage());
		// Calc CRC and write it to the flash
		final int flashCRC = binaryImage.calcCRC();
		log.debug("CRC: " + flashCRC);
		helper.writeCRCtoFlash(flashCRC);

		// Write program to flash
		BinaryImageBlock block;
		int blockCount = 3;
		int blockNumber = 3; // blockNumber != blockCount because block 8 & 9 ==
		int blocksWritten = 0;

		// 32 kb all other 4 kb
		while ((block = binaryImage.getNextBlock()) != null) {

			final byte[] data = block.getData();
			final int address = block.getAddress();

			try {
				helper.writeToRAM(PacemateHelper.START_ADDRESS_IN_RAM, data.length);
			} catch (Exception e) {
				log.error("Error while write to RAM! Program Operation will be cancelled!", e);
				throw e;
			}

			int counter = 0;
			int lineCounter = 0;

			byte[] line;

			// each block is sent in parts of 20 lines a 45 bytes
			while (counter < data.length) {
				int offset = 0;
				int bytesNotYetApproved = 0;
				if (counter + 45 < data.length) {
					line = new byte[PacemateBinaryImage.LINESIZE]; // a line with 45
					// bytes
					System.arraycopy(data, counter, line, 0, PacemateBinaryImage.LINESIZE);
					counter = counter + PacemateBinaryImage.LINESIZE;
					bytesNotYetApproved = bytesNotYetApproved + PacemateBinaryImage.LINESIZE;
				} else {
					if (((data.length - counter) % 3) == 1) {
						offset = 2;
					} else if (((data.length - counter) % 3) == 2) {
						offset = 1;
					}
					line = new byte[data.length - counter + offset];
					line[line.length - 1] = 0;
					line[line.length - 2] = 0;
					System.arraycopy(data, counter, line, 0, data.length - counter);
					counter = counter + (data.length - counter);
					bytesNotYetApproved = bytesNotYetApproved + (data.length - counter);
				}

				try {
					helper.sendDataMessage(binaryImage.encode(line, line.length - offset));
				} catch (Exception e) {
					log.error("Error while writing flash! OperationRunnable will be cancelled!", e);
					throw e;
				}

				lineCounter++;
				if ((lineCounter == 20) || (counter >= data.length)) {
					try {
						helper.sendChecksum(binaryImage.crc);
					} catch (InvalidChecksumException e) {
						log.debug("Invalid Checksum - resend last part");
						// so resending the last 20 lines
						counter = counter - bytesNotYetApproved;
					} catch (Exception e) {
						log.debug("Error while writing flash! OperationRunnable will be cancelled!", e);
						throw e;
					}
					lineCounter = 0;
					binaryImage.crc = 0;
				}
			}

			try {
				// if block is completed copy data from RAM to Flash
				log.debug("Prepare Flash and Copy Ram to Flash " + blockCount + " " + blockNumber + " " + address);
				helper.configureFlash(blockNumber, blockNumber);
				if (data.length > 1024) {
					helper.copyRAMToFlash(address, PacemateHelper.START_ADDRESS_IN_RAM, 4096);
				} else if (data.length > 512) {
					helper.copyRAMToFlash(address, PacemateHelper.START_ADDRESS_IN_RAM, 1024);
				} else if (data.length > 512) {
					helper.copyRAMToFlash(address, PacemateHelper.START_ADDRESS_IN_RAM, 512);
				} else {
					helper.copyRAMToFlash(address, PacemateHelper.START_ADDRESS_IN_RAM, 256);
				}
			} catch (Exception e) {
				log.error("Error while copy RAM to Flash! OperationRunnable will be cancelled!", e);
				throw e;
			}

			// Notify listeners of the new status
			blocksWritten++;
			progress(0.125f + PROGRESS_FRACTION_PROGRAM * (1.0f / binaryImage.getBlockCount()) * blocksWritten);

			// Return with success if the user has requested to cancel this
			// operation
			if (isCanceled()) {
				return;
			}

			blockCount++;
			if ((blockCount > 0) && (8 >= blockCount)) // Sector 0-7 4kb
			{
				blockNumber++;
			} else if (blockCount == 16) // Sector 8 32kb
			{
				blockNumber++;
			} else if (blockCount == 24) // Sector 9 32kb
			{
				blockNumber++;
			} else if (blockCount == 32) // Sector 10 32kb
			{
				blockNumber++;
			} else if (blockCount == 40) // Sector 11 32kb
			{
				blockNumber++;
			} else if (blockCount == 48) // Sector 12 32kb
			{
				blockNumber++;
			} else if (blockCount == 56) // Sector 13 32kb
			{
				blockNumber++;
			} else if (blockCount == 64) // Sector 14 32kb
			{
				blockNumber++;
			}
		}

		progress(0.125f + PROGRESS_FRACTION_PROGRAM * 1.0f);
	}

	@Override
	@SerialPortProgrammingMode
	protected Void callInternal() throws Exception {

		log.trace("Program operation executing...");

		runSubOperation(operationFactory.createEraseFlashOperation(120000, null), 0.125f);
		program();

		log.trace("Program operation finished");

		return null;
	}
}
