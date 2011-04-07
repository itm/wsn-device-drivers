package de.uniluebeck.itm.rsc.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.rsc.drivers.core.ChipType;
import de.uniluebeck.itm.rsc.drivers.core.MacAddress;
import de.uniluebeck.itm.rsc.drivers.core.Monitor;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractWriteMacAddressOperation;
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
	
	private void writeMacAddress(final ChipType chipType, final Monitor monitor) throws Exception {
		final MacAddress macAddress = getMacAddress();
		
		// Wait for a connection
		while (!isCanceled() && !device.waitForConnection()) {
			log.debug("Still waiting for a connection");
		}

		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			return;
		}
		
		// Read the first sector
		byte[][] sector = readSector(monitor, Sector.FIRST);

		// Check if this operation has been cancelled
		if (isCanceled()) {
			return;
		}

		// Copy address into the header of the first sector
		log.trace("Copy " + StringUtils.toHexString(macAddress.getMacBytes()) + " to address " + chipType.getHeaderStart() + ", length: "
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
	}
	
	@Override
	public Void execute(final Monitor monitor) throws Exception {
		log.trace("Writing mac address...");
		final ChipType chipType = executeSubOperation(device.createGetChipTypeOperation(), monitor);
		// Check if the user has cancelled the operation
		if (isCanceled()) {
			return null;
		}
		executeSubOperation(device.createEnterProgramModeOperation(), monitor);
		try {
			writeMacAddress(chipType, monitor);
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), monitor);
		}
		log.trace("Done, written MAC Address: " + getMacAddress());
		return null;
	}
	
	protected byte[][] readSector(Monitor monitor, Sector index) throws Exception {
		int start = index.getStart();
		int length = index.getEnd() - start;

		// Calculate number of blocks to read
		int totalBlocks = length / BLOCKSIZE;
		int residue = length - totalBlocks * BLOCKSIZE;

		log.trace(String.format("length = %d, totalBlocks = %d, residue = %d", length, totalBlocks, residue));

		// Prepare byte array
		byte[][] sector = new byte[totalBlocks + (residue > 0 ? 1 : 0)][BLOCKSIZE];

		// Read block after block
		int address = start;
		for (int readBlocks = 0; readBlocks < totalBlocks; readBlocks++) {
			sector[readBlocks] = device.readFlash(address, BLOCKSIZE);
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
			sector[sector.length - 1] = device.readFlash(address, residue);
		}
		return sector;
	}
	
	private void writeSector(Monitor monitor, Sector index, byte[][] sector) throws Exception {
		int address = index.getStart();
		for (int i = 0; i < sector.length; ++i) {
			log.trace("Writing sector " + index + ", block " + i + ": " + StringUtils.toHexString(sector[i]));
			
			device.writeFlash(address, sector[i]);
			
			address += sector[i].length;
			float progress = 0.5f + (i + 1.0f) / (sector.length * 2.0f);
			monitor.onProgressChange(progress);
		}
	}
}
