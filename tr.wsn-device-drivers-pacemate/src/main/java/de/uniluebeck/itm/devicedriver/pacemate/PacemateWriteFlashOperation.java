package de.uniluebeck.itm.devicedriver.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.exception.InvalidChecksumException;
import de.uniluebeck.itm.devicedriver.operation.AbstractWriteFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.util.BinDataBlock;

public class PacemateWriteFlashOperation extends AbstractWriteFlashOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateWriteFlashOperation.class);
	
	private final PacemateDevice device;
	
	public PacemateWriteFlashOperation(PacemateDevice device) {
		this.device = device;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		// Enter programming mode
		executeSubOperation(device.createEnterProgramModeOperation());
		device.clearStreamData();
		device.autobaud();

		// device.echoOff();

		// Wait for a connection
		while (!isCanceled() && !device.waitForConnection()) {
			log.info("Still waiting for a connection");
		}

		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			log.debug("Operation has been cancelled");
			return null;
		}

		// Connection established, determine chip type
		GetChipTypeOperation getChipTypeOperation = device.createGetChipTypeOperation();
		ChipType chipType = executeSubOperation(getChipTypeOperation);
		log.debug("Chip type is " + chipType);

		final PacemateBinData binData = new PacemateBinData(address, data);

		try {
			device.configureFlash();
		} catch (Exception e) {
			log.debug("Error while configure flash! Operation will be cancelled!");
			throw e;
		}

		try {
			device.eraseFlash(address, address + length);
		} catch (Exception e) {
			log.debug("Error while erasing! Operation will be cancelled!");
			throw e;
		}

		int flashCRC = binData.calcCRC();

		log.debug("CRC " + flashCRC);

		try {
			device.writeCRCtoFlash(flashCRC);
		} catch (Exception e) {
			log.debug("Error while write CRC to Flash! Operation will be cancelled!");
			throw e;
		}

		// Write program to flash
		BinDataBlock block = null;
		int blockCount = 3;
		int blockNumber = 3; // blockNumber != blockCount because block 8 & 9 == 32 kb all other 4 kb
		while ((block = binData.getNextBlock()) != null) {
			try {
				device.writeToRAM(PacemateDevice.START_ADDRESS_IN_RAM, block.data.length);
			} catch (Exception e) {
				log.debug("Error while write to RAM! Operation will be cancelled!");
				return null;
			}

			int counter = 0;
			int linecounter = 0;

			byte[] line = null;

			// each block is sent in parts of 20 lines a 45 bytes
			while (counter < block.data.length) {
				int offset = 0;
				int bytesNotYetProoved = 0;
				if (counter + 45 < block.data.length) {
					line = new byte[PacemateBinData.LINESIZE]; // a line with 45 bytes
					System.arraycopy(block.data, counter, line, 0, PacemateBinData.LINESIZE);
					counter = counter + PacemateBinData.LINESIZE;
					bytesNotYetProoved = bytesNotYetProoved + PacemateBinData.LINESIZE;
				} else {
					if (((block.data.length - counter) % 3) == 1) {
						offset = 2;
					} else if (((block.data.length - counter) % 3) == 2) {
						offset = 1;
					}
					line = new byte[block.data.length - counter + offset];
					line[line.length - 1] = 0;
					line[line.length - 2] = 0;
					System.arraycopy(block.data, counter, line, 0, block.data.length - counter);
					counter = counter + (block.data.length - counter);
					bytesNotYetProoved = bytesNotYetProoved + (block.data.length - counter);
				}

				// System.out.println("Sending data msg: " + Tools.toASCIIString(line));

				// printLine(pacemateProgram.encode(line,(line.length -offset)));

				try {
					device.sendDataMessage(binData.encode(line, (line.length - offset)));
				} catch (Exception e) {
					log.debug("Error while writing flash! Operation will be cancelled!");
					return null;
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
						log.debug("Error while writing flash! Operation will be cancelled!");
						return null;
					}
					linecounter = 0;
					// System.out.println("CRC "+pacemateProgram.crc);
					binData.crc = 0;
					bytesNotYetProoved = 0;
				}
			}

			try {
				// if block is completed copy data from RAM to Flash
				System.out.println("Prepare Flash and Copy Ram to Flash " + blockCount + " " + blockNumber + " "
						+ block.address);
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
				log.debug("Error while copy RAM to Flash! Operation will be cancelled!");
				return null;
			}

			// Notify listeners of the new status
			float progress = ((float) (blockCount - 2)) / ((float) binData.getBlockCount());
			monitor.onProgressChange(progress);

			// Return with success if the user has requested to cancel this
			// operation
			if (isCanceled()) {
				log.debug("Operation has been cancelled");
				return null;
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
		return null;
	}

}
