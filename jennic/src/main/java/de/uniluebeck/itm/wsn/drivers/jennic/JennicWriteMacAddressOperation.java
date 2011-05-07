package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractWriteMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

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
	
	private void writeMacAddress(final ChipType chipType, final ProgressManager progressManager) throws Exception {
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
		byte[][] sector = readSector(progressManager.createSub(0.875f), Sector.FIRST);

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
		writeSector(progressManager.createSub(0.125f), Sector.FIRST, sector);
	}
	
	@Override
	public Void execute(final ProgressManager progressManager) throws Exception {
		log.trace("Writing mac address...");
		final ChipType chipType = executeSubOperation(device.createGetChipTypeOperation(), progressManager.createSub(0.0625f));
		// Check if the user has cancelled the operation
		if (isCanceled()) {
			return null;
		}
		executeSubOperation(device.createEnterProgramModeOperation(), progressManager.createSub(0.0625f));
		try {
			writeMacAddress(chipType, progressManager.createSub(0.8125f));
		} finally {
			executeSubOperation(device.createLeaveProgramModeOperation(), progressManager.createSub(0.0625f));
		}
		log.trace("Done, written MAC Address: " + getMacAddress());
		return null;
	}
	
	protected byte[][] readSector(final ProgressManager progressManager, final Sector index) throws Exception {
		final int start = index.getStart();
		final int length = index.getEnd() - start;

		// Calculate number of blocks to read
		final int totalBlocks = length / BLOCKSIZE;
		final int residue = length - totalBlocks * BLOCKSIZE;

		log.trace(String.format("length = %d, totalBlocks = %d, residue = %d", length, totalBlocks, residue));

		// Prepare byte array
		final byte[][] sector = new byte[totalBlocks + (residue > 0 ? 1 : 0)][BLOCKSIZE];

		// Read block after block
		final float worked = 1.0f / totalBlocks;
		int address = start;
		for (int readBlocks = 0; readBlocks < totalBlocks; readBlocks++) {
			sector[readBlocks] = device.readFlash(address, BLOCKSIZE);
			address += BLOCKSIZE;

			progressManager.worked(worked);

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
	
	private void writeSector(final ProgressManager progressManager, final Sector index, final byte[][] sector) throws Exception {
		final float worked = 1.0f / sector.length;
		int address = index.getStart();
		for (int i = 0; i < sector.length; ++i) {
			log.trace("Writing sector " + index + ", block " + i + ": " + StringUtils.toHexString(sector[i]));
			
			device.writeFlash(address, sector[i]);
			
			address += sector[i].length;
			progressManager.worked(worked);
		}
	}
}
