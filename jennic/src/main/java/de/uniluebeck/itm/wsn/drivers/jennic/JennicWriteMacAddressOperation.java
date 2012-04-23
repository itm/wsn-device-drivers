package de.uniluebeck.itm.wsn.drivers.jennic;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractWriteMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationFactory;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JennicWriteMacAddressOperation extends AbstractWriteMacAddressOperation {

	private static final Logger log = LoggerFactory.getLogger(JennicWriteMacAddressOperation.class);

	private static final int BLOCK_SIZE = 128;

	private static final float FRACTION_READ_FIRST_SECTOR = 0.49f;

	private static final float FRACTION_WRITE_FIRST_SECTOR = 0.49f;

	private static final float FRACTION_GET_CHIP_TYPE = 0.02f;

	private final JennicHelper helper;

	private final OperationFactory operationFactory;

	@Inject
	public JennicWriteMacAddressOperation(final TimeLimiter timeLimiter,
										  final JennicHelper helper,
										  final OperationFactory operationFactory,
										  @Assisted final MacAddress macAddress,
										  @Assisted final long timeoutMillis,
										  @Assisted final OperationListener<Void> operationCallback) {
		super(timeLimiter, macAddress, timeoutMillis, operationCallback);
		this.helper = helper;
		this.operationFactory = operationFactory;
	}

	private void writeMacAddress(ChipType chipType) throws Exception {

		// Wait for a helper
		while (!isCanceled() && !helper.waitForConnection()) {
			log.debug("Still waiting for a connection");
		}

		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			return;
		}

		// Read the first sector
		byte[][] blocksFirstSector = readSector(Sector.FIRST);
		progress(FRACTION_GET_CHIP_TYPE + FRACTION_READ_FIRST_SECTOR);

		// Check if this operation has been cancelled
		if (isCanceled()) {
			return;
		}

		// Copy address into the header of the first sector
		byte[] macAddressBytes = macAddress.toByteArray();

		if (log.isTraceEnabled()) {
			log.trace("Copy MAC address bytes ({}) to address {}, length: {}", new Object[]{
					StringUtils.toHexString(macAddressBytes),
					chipType.getHeaderStart(),
					macAddressBytes.length
			}
			);
		}

		System.arraycopy(macAddressBytes, 0, blocksFirstSector[0], chipType.getHeaderStart(), macAddressBytes.length);

		helper.configureFlash(chipType);
		helper.eraseFlash(Sector.FIRST);

		writeSector(Sector.FIRST, blocksFirstSector);
		progress(
				FRACTION_GET_CHIP_TYPE + FRACTION_READ_FIRST_SECTOR + FRACTION_WRITE_FIRST_SECTOR
		);
	}

	protected byte[][] readSector(final Sector index) throws Exception {

		final int start = index.getStart();
		final int length = index.getEnd() - start;

		// Calculate number of blocks to read
		final int totalBlocks = length / BLOCK_SIZE;
		final int residue = length - totalBlocks * BLOCK_SIZE;

		log.trace(String.format("length = %d, totalBlocks = %d, residue = %d", length, totalBlocks, residue));

		// Prepare byte array
		final byte[][] sector = new byte[totalBlocks + (residue > 0 ? 1 : 0)][BLOCK_SIZE];

		// Read block after block
		int address = start;
		for (int readBlocks = 0; readBlocks < totalBlocks; readBlocks++) {

			sector[readBlocks] = helper.readFlash(address, BLOCK_SIZE);
			address += BLOCK_SIZE;

			progress(
					FRACTION_GET_CHIP_TYPE + (FRACTION_READ_FIRST_SECTOR * ((float) readBlocks / (float) totalBlocks))
			);

			// Check if the user has cancelled the operation
			if (isCanceled()) {
				log.debug("Sector read has been cancelled");
				return null;
			}
		}

		// Read residue
		if (residue > 0) {
			sector[sector.length - 1] = helper.readFlash(address, residue);
		}

		return sector;
	}

	private void writeSector(final Sector sector, final byte[][] blocks) throws Exception {

		int address = sector.getStart();

		for (int block = 0; block < blocks.length; ++block) {

			log.trace("Writing {} sector, block {}", sector, block);

			helper.writeFlash(address, blocks[block]);

			address += blocks[block].length;

			progress(
					FRACTION_GET_CHIP_TYPE + FRACTION_READ_FIRST_SECTOR + (FRACTION_WRITE_FIRST_SECTOR * ((float) block / (float) blocks.length))
			);
		}
	}

	@Override
	@SerialPortProgrammingMode
	protected Void callInternal() throws Exception {

		log.trace("Writing mac address...");
		ChipType chipType = runSubOperation(
				operationFactory.createGetChipTypeOperation(1000, null),
				FRACTION_GET_CHIP_TYPE
		);
		writeMacAddress(chipType);
		log.trace("Done, written MAC Address: " + getMacAddress());
		return null;
	}
}
