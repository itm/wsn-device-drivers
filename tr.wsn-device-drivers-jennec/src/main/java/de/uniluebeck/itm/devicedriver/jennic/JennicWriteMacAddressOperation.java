package de.uniluebeck.itm.devicedriver.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractWriteMacAddressOperation;
import de.uniluebeck.itm.devicedriver.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteFlashOperation;
import de.uniluebeck.itm.tr.util.StringUtils;

public class JennicWriteMacAddressOperation extends AbstractWriteMacAddressOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicWriteMacAddressOperation.class);
	
	private static final int BLOCKSIZE = 128;
	
	private final JennicDevice device;
	
	public JennicWriteMacAddressOperation(JennicDevice device) {
		this.device = device;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		// Enter programming mode
		EnterProgramModeOperation enterProgramModeOperation = device.createEnterProgramModeOperation();
		executeSubOperation(enterProgramModeOperation, monitor);

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

		// Connection established, determine chip type and configure the Flash
		// chip
		GetChipTypeOperation getChipTypeOperation = device.createGetChipTypeOperation();
		ChipType chipType = executeSubOperation(getChipTypeOperation, monitor);
		log.debug("Chip type is " + chipType);

		// Check if the user has cancelled the operation
		if (isCanceled()) {
			log.debug("Operation has been cancelled");
			return null;
		}

		// Read the first sector
		byte[][] sector = readSector(monitor, Sector.FIRST);

		// Check if this operation has been cancelled
		if (isCanceled()) {
			return null;
		}

		// Copy address into the header of the first sector
		log.debug("Copy " + StringUtils.toHexString(macAddress.getMacBytes()) + " to address " + chipType.getHeaderStart() + ", length: "
				+ macAddress.getMacBytes().length);
		// System.arraycopy(mac.getMacBytes(), 0, sector[0][0], ChipType.
		// getHeaderStart(chipType),
		// mac.getMacBytes().length);
		System.arraycopy(macAddress.getMacBytes(), 0, sector[0], chipType.getHeaderStart(), macAddress.getMacBytes().length);

		// Configure flash
		device.configureFlash(chipType);

		// Erase flash sector 0
		device.eraseFlash(Sector.FIRST);

		// Write sector 0 with the new MAC
		writeSector(monitor, Sector.FIRST, sector);

		log.debug("Done, written MAC Address: " + macAddress);
		
		final LeaveProgramModeOperation leaveProgramModeOperation = device.createLeaveProgramModeOperation();
		executeSubOperation(leaveProgramModeOperation, monitor);
		
		return null;
	}
	
	protected byte[][] readSector(Monitor monitor, Sector index) throws Exception {
		int start = index.getStart();
		int length = index.getEnd() - start;

		// Calculate number of blocks to read
		int totalBlocks = length / BLOCKSIZE;
		int residue = length - totalBlocks * BLOCKSIZE;

		log.debug(String.format("length = %d, totalBlocks = %d, residue = %d", length, totalBlocks, residue));

		// Prepare byte array
		byte[][] sector = new byte[totalBlocks + (residue > 0 ? 1 : 0)][BLOCKSIZE];

		// Read block after block
		int address = start;
		for (int readBlocks = 0; readBlocks < totalBlocks; readBlocks++) {
			final ReadFlashOperation readFlashOperation = device.createReadFlashOperation();
			readFlashOperation.setAddress(address, BLOCKSIZE);
			sector[readBlocks] = executeSubOperation(readFlashOperation, monitor);
			address += BLOCKSIZE;

			float progress = ((float) readBlocks) / ((float) (totalBlocks * 2));
			monitor.onProgressChange(progress);

			// Check if the user has cancelled the operation
			if (isCanceled()) {
				log.debug("Sector read has been cancelled");
				return null;
			}
		}

		// Read residue
		if (residue > 0) {
			final ReadFlashOperation readFlashOperation = device.createReadFlashOperation();
			readFlashOperation.setAddress(address, residue);
			sector[sector.length - 1] = executeSubOperation(readFlashOperation, monitor);
		}
		return sector;
	}
	
	private void writeSector(Monitor monitor, Sector index, byte[][] sector) throws Exception {
		int address = index.getStart();
		for (int i = 0; i < sector.length; ++i) {
			log.debug("Writing sector " + index + ", block " + i + ": " + StringUtils.toHexString(sector[i]));
			
			WriteFlashOperation writeFlashOperation = device.createWriteFlashOperation();
			writeFlashOperation.setData(address, sector[i], sector[i].length);
			executeSubOperation(writeFlashOperation, monitor);
			
			address += sector[i].length;
			float progress = 0.5f + (i + 1.0f) / (sector.length * 2.0f);
			monitor.onProgressChange(progress);
		}
	}
}
