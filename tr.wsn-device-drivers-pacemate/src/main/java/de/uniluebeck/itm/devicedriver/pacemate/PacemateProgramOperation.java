package de.uniluebeck.itm.devicedriver.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.exception.InvalidChecksumException;
import de.uniluebeck.itm.devicedriver.operation.AbstractProgramOperation;
import de.uniluebeck.itm.devicedriver.util.BinDataBlock;

public class PacemateProgramOperation extends AbstractProgramOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateProgramOperation.class);

	private final PacemateDevice device;

	public PacemateProgramOperation(final PacemateDevice device) {
		this.device = device;
	}
	
	private void program(final Monitor monitor) throws Exception {
		device.clearStreamData();
		device.autobaud();

		// Wait for a connection
		while (!isCanceled() && !device.waitForConnection()) {
			log.info("Still waiting for a connection...");
		}

		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			return;
		}

		// Erase the complete flash
		executeSubOperation(device.createEraseFlashOperation());

		// Create pacemate image
		final PacemateBinData binData = new PacemateBinData(binaryImage);
		// Calc CRC and write it to the flash
		final int flashCRC = binData.calcCRC();
		log.debug("CRC: " + flashCRC);
		device.writeCRCtoFlash(flashCRC);

		// Write program to flash
		BinDataBlock block = null;
		int blockCount = 3;
		int blockNumber = 3; // blockNumber != blockCount because block 8 & 9 ==
								// 32 kb all other 4 kb
		while ((block = binData.getNextBlock()) != null) {
			try {
				device.writeToRAM(PacemateDevice.START_ADDRESS_IN_RAM, block.data.length);
			} catch (Exception e) {
				log.error("Error while write to RAM! Operation will be cancelled!", e);
				throw e;
			}

			int counter = 0;
			int linecounter = 0;

			byte[] line = null;

			// each block is sent in parts of 20 lines a 45 bytes
			while (counter < block.data.length) {
				int offset = 0;
				int bytesNotYetProoved = 0;
				if (counter + 45 < block.data.length) {
					line = new byte[PacemateBinData.LINESIZE]; // a line with 45
																// bytes
					System.arraycopy(block.data, counter, line, 0, PacemateBinData.LINESIZE);
					counter = counter + PacemateBinData.LINESIZE;
					bytesNotYetProoved = bytesNotYetProoved + PacemateBinData.LINESIZE;
				} else {
					if (((block.data.length - counter) % 3) == 1)
						offset = 2;
					else if (((block.data.length - counter) % 3) == 2)
						offset = 1;
					line = new byte[block.data.length - counter + offset];
					line[line.length - 1] = 0;
					line[line.length - 2] = 0;
					System.arraycopy(block.data, counter, line, 0, block.data.length - counter);
					counter = counter + (block.data.length - counter);
					bytesNotYetProoved = bytesNotYetProoved + (block.data.length - counter);
				}

				try {
					device.sendDataMessage(binData.encode(line, line.length - offset));
				} catch (Exception e) {
					log.error("Error while writing flash! Operation will be cancelled!", e);
					throw e;
				}

				linecounter++;
				if ((linecounter == 20) || (counter >= block.data.length)) {
					try {
						device.sendChecksum(binData.crc);
					} catch (InvalidChecksumException e) {
						log.debug("Invalid Checksum - resend last part");
						// so resending the last 20 lines
						counter = counter - bytesNotYetProoved;
					} catch (Exception e) {
						log.debug("Error while writing flash! Operation will be cancelled!", e);
						throw e;
					}
					linecounter = 0;
					binData.crc = 0;
					bytesNotYetProoved = 0;
				}
			}

			try {
				// if block is completed copy data from RAM to Flash
				System.out.println("Prepare Flash and Copy Ram to Flash "
						+ blockCount + " " + blockNumber + " " + block.address);
				device.configureFlash(blockNumber, blockNumber);
				if (block.data.length > 1024) {
					device.copyRAMToFlash(block.address, PacemateDevice.START_ADDRESS_IN_RAM, 4096);
				} else if (block.data.length > 512) {
					device.copyRAMToFlash(block.address, PacemateDevice.START_ADDRESS_IN_RAM, 1024);
				} else if (block.data.length > 512) {
					device.copyRAMToFlash(block.address, PacemateDevice.START_ADDRESS_IN_RAM, 512);
				} else {
					device.copyRAMToFlash(block.address, PacemateDevice.START_ADDRESS_IN_RAM, 256);
				}
			} catch (Exception e) {
				log.error("Error while copy RAM to Flash! Operation will be cancelled!", e);
				throw e;
			}

			// Notify listeners of the new status
			float progress = ((float) (blockCount - 2)) / ((float) binData.getBlockCount());
			monitor.onProgressChange(progress);

			// Return with success if the user has requested to cancel this
			// operation
			if (isCanceled()) {
				return;
			}

			blockCount++;
			if ((blockCount > 0) && (8 >= blockCount)) // Sektor 0-7 4kb
			{
				blockNumber++;
			} else if (blockCount == 16) // Sektor 8 32kb
			{
				blockNumber++;
			} else if (blockCount == 24) // Sektor 9 32kb
			{
				blockNumber++;
			} else if (blockCount == 32) // Sektor 10 32kb
			{
				blockNumber++;
			} else if (blockCount == 40) // Sektor 11 32kb
			{
				blockNumber++;
			} else if (blockCount == 48) // Sektor 12 32kb
			{
				blockNumber++;
			} else if (blockCount == 56) // Sektor 13 32kb
			{
				blockNumber++;
			} else if (blockCount == 64) // Sektor 14 32kb
			{
				blockNumber++;
			}
		}
	}

	@Override
	public Void execute(final Monitor monitor) throws Exception {
		log.debug("Prgramming operation executing...");
		// Enter programming mode
		executeSubOperation(device.createEnterProgramModeOperation());
		try {
			program(monitor);
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation());
		}		
		log.debug("Program operation finsihed");
		return null;
	}

}
