package de.uniluebeck.itm.wsn.drivers.pacemate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.exception.InvalidChecksumException;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.util.BinDataBlock;

public class PacemateProgramOperation extends AbstractProgramOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateProgramOperation.class);

	private final PacemateSerialPortConnection connection;
	
	private final EnterProgramModeOperation enterProgramModeOperation;
	
	private final LeaveProgramModeOperation leaveProgramModeOperation;
	
	private final EraseFlashOperation eraseFlashOperation;

	@Inject
	public PacemateProgramOperation(PacemateSerialPortConnection connection,
			EnterProgramModeOperation enterProgramModeOperation,
			LeaveProgramModeOperation leaveProgramModeOperation,
			EraseFlashOperation eraseFlashOperation) {
		this.connection = connection;
		this.enterProgramModeOperation = enterProgramModeOperation;
		this.leaveProgramModeOperation = leaveProgramModeOperation;
		this.eraseFlashOperation = eraseFlashOperation;
	}
	
	private void program(final ProgressManager progressManager) throws Exception {		
		connection.clearStreamData();
		connection.autobaud();

		connection.waitForBootLoader();

		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			return;
		}

		// Create pacemate image
		final PacemateBinData binData = new PacemateBinData(getBinaryImage());
		// Calc CRC and write it to the flash
		final int flashCRC = binData.calcCRC();
		log.debug("CRC: " + flashCRC);
		connection.writeCRCtoFlash(flashCRC);

		// Write program to flash
		BinDataBlock block = null;
		int blockCount = 3;
		int blockNumber = 3; // blockNumber != blockCount because block 8 & 9 ==
								// 32 kb all other 4 kb
		while ((block = binData.getNextBlock()) != null) {
			final byte[] data = block.getData();
			final int address = block.getAddress();
			
			try {
				connection.writeToRAM(PacemateSerialPortConnection.START_ADDRESS_IN_RAM, data.length);
			} catch (Exception e) {
				log.error("Error while write to RAM! Operation will be cancelled!", e);
				throw e;
			}

			int counter = 0;
			int linecounter = 0;

			byte[] line = null;

			// each block is sent in parts of 20 lines a 45 bytes
			while (counter < data.length) {
				int offset = 0;
				int bytesNotYetProoved = 0;
				if (counter + 45 < data.length) {
					line = new byte[PacemateBinData.LINESIZE]; // a line with 45
																// bytes
					System.arraycopy(data, counter, line, 0, PacemateBinData.LINESIZE);
					counter = counter + PacemateBinData.LINESIZE;
					bytesNotYetProoved = bytesNotYetProoved + PacemateBinData.LINESIZE;
				} else {
					if (((data.length - counter) % 3) == 1)
						offset = 2;
					else if (((data.length - counter) % 3) == 2)
						offset = 1;
					line = new byte[data.length - counter + offset];
					line[line.length - 1] = 0;
					line[line.length - 2] = 0;
					System.arraycopy(data, counter, line, 0, data.length - counter);
					counter = counter + (data.length - counter);
					bytesNotYetProoved = bytesNotYetProoved + (data.length - counter);
				}

				try {
					connection.sendDataMessage(binData.encode(line, line.length - offset));
				} catch (Exception e) {
					log.error("Error while writing flash! Operation will be cancelled!", e);
					throw e;
				}

				linecounter++;
				if ((linecounter == 20) || (counter >= data.length)) {
					try {
						connection.sendChecksum(binData.crc);
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
				log.debug("Prepare Flash and Copy Ram to Flash " + blockCount + " " + blockNumber + " " + address);
				connection.configureFlash(blockNumber, blockNumber);
				if (data.length > 1024) {
					connection.copyRAMToFlash(address, PacemateSerialPortConnection.START_ADDRESS_IN_RAM, 4096);
				} else if (data.length > 512) {
					connection.copyRAMToFlash(address, PacemateSerialPortConnection.START_ADDRESS_IN_RAM, 1024);
				} else if (data.length > 512) {
					connection.copyRAMToFlash(address, PacemateSerialPortConnection.START_ADDRESS_IN_RAM, 512);
				} else {
					connection.copyRAMToFlash(address, PacemateSerialPortConnection.START_ADDRESS_IN_RAM, 256);
				}
			} catch (Exception e) {
				log.error("Error while copy RAM to Flash! Operation will be cancelled!", e);
				throw e;
			}

			// Notify listeners of the new status
			progressManager.worked(1.0f / binData.getBlockCount());

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
		progressManager.done();
	}

	@Override
	public Void execute(final ProgressManager progressManager) throws Exception {
		log.debug("Prgramming operation executing...");
		// Erase the complete flash
		executeSubOperation(eraseFlashOperation, progressManager.createSub(0.125f));
		
		// Now program the device
		executeSubOperation(enterProgramModeOperation, progressManager.createSub(0.0625f));
		try {
			program(progressManager.createSub(0.75f));
		} finally {
			executeSubOperation(leaveProgramModeOperation, progressManager.createSub(0.0625f));
		}		
		log.debug("Program operation finsihed");
		return null;
	}

}
