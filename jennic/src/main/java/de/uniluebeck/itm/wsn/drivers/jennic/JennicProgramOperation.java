package de.uniluebeck.itm.wsn.drivers.jennic;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.exception.*;
import de.uniluebeck.itm.wsn.drivers.core.operation.*;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import de.uniluebeck.itm.wsn.drivers.core.util.BinaryImageBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;

public class JennicProgramOperation extends AbstractProgramOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicProgramOperation.class);

	private static final float FRACTION_GET_CHIP_TYPE = 0.01f;

	private static final float FRACTION_READ_MAC_FROM_DEVICE = 0.02f;

	private static final float FRACTION_PROGRAM_WRITE_IMAGE = 0.92f;

	private static final float FRACTION_RESET = 0.03f;

	private final JennicHelper helper;

	private final OperationFactory operationFactory;

	@Inject
	public JennicProgramOperation(final TimeLimiter timeLimiter,
								  final JennicHelper helper,
								  final OperationFactory operationFactory,
								  @Assisted byte[] binaryImage,
								  @Assisted final long timeoutMillis,
								  @Assisted @Nullable final OperationListener<Void> operationCallback) {

		super(timeLimiter, binaryImage, timeoutMillis, operationCallback);
		this.helper = helper;
		this.operationFactory = operationFactory;
	}

	@Override
	@SerialPortProgrammingMode
	protected Void callInternal() throws Exception {

		GetChipTypeOperation getChipTypeOperation = operationFactory.createGetChipTypeOperation(10000, null);
		ChipType chipType = runSubOperation(getChipTypeOperation, FRACTION_GET_CHIP_TYPE);

		JennicBinaryImage binaryImage = new JennicBinaryImage(getBinaryImage());
		assertImageCompatible(binaryImage, chipType);

		final MacAddress macAddressBefore = readMacAddress(chipType);

		if (isBrokenMacAddress(macAddressBefore)) {
			throw new FlashProgramFailedException("Device MAC address (" + macAddressBefore + ") is broken!");
		}

		writeMacAddressToImage(macAddressBefore, binaryImage);

		while (!isCanceled() && !helper.waitForConnection()) {
			log.debug("Waiting for a connection...");
		}

		if (isCanceled()) {
			return null;
		}

		eraseSectors(chipType);
		writeBinaryImage(binaryImage);

		final MacAddress macAddressAfter = readMacAddress(chipType);

		// if MAC address is broken after flashing try to rewrite the old MAC address that was there before
		if (isBrokenMacAddress(macAddressAfter)) {

			// write old MAC address
			runSubOperation(operationFactory.createWriteMacAddressOperation(macAddressBefore, 2000, null), 0f);

			// if MAC address is still broken, abort
			if (isBrokenMacAddress(readMacAddress(chipType))) {
				throw new FlashProgramFailedException(
						"After flashing the MAC address seems to be " + macAddressAfter + " which may result in unexpected behavior!"
				);
			}
		}

		runSubOperation(operationFactory.createResetOperation(1000, null), FRACTION_RESET);

		return null;
	}

	private boolean isBrokenMacAddress(final MacAddress macAddress) throws Exception {
		return MacAddress.HIGHEST_MAC_ADDRESS.equals(macAddress);
	}

	private void writeBinaryImage(final JennicBinaryImage binaryImage)
			throws IOException, TimeoutException, UnexpectedResponseException, InvalidChecksumException,
			FlashProgramFailedException {

		BinaryImageBlock block;

		int blockNr = 0;
		int blockCount = binaryImage.getBlockCount();

		while ((block = binaryImage.getNextBlock()) != null) {

			blockNr++;

			if (log.isTraceEnabled()) {
				log.trace("Writing block {} of {}", blockNr, blockCount);
			}

			helper.writeFlash(block.getAddress(), block.getData());

			final float progressBefore = FRACTION_GET_CHIP_TYPE + FRACTION_READ_MAC_FROM_DEVICE;
			progress(progressBefore + (FRACTION_PROGRAM_WRITE_IMAGE * ((float) blockNr / (float) blockCount)));
		}
	}

	private void eraseSectors(final ChipType chipType) throws Exception {
		helper.configureFlash(chipType);
		helper.eraseFlash(Sector.FIRST);
		helper.eraseFlash(Sector.SECOND);
		helper.eraseFlash(Sector.THIRD);
	}

	private void writeMacAddressToImage(final MacAddress macAddress, final JennicBinaryImage binaryImage)
			throws ProgramChipMismatchException {
		binaryImage.insertHeader(macAddress.toByteArray());
	}

	private MacAddress readMacAddress(final ChipType chipType) throws Exception {
		return new MacAddress(readMacAddressBytes(chipType));
	}

	private byte[] readMacAddressBytes(final ChipType chipType) throws Exception {
		return readDeviceFlashHeader(chipType.getHeaderStart(), chipType.getHeaderLength());
	}

	private byte[] readDeviceFlashHeader(final int address, final int length) throws Exception {
		ReadFlashOperation subOperation = operationFactory.createReadFlashOperation(address, length, 120000, null);
		return runSubOperation(subOperation, FRACTION_READ_MAC_FROM_DEVICE);
	}

	private void assertImageCompatible(final JennicBinaryImage binaryImage, final ChipType chipType) throws Exception {

		if (!binaryImage.isCompatible(chipType)) {
			log.error("Device chip type ({}) and image chip type ({}) mismatch!", chipType, binaryImage.getChipType());
			throw new ProgramChipMismatchException(chipType, binaryImage.getChipType());
		}
	}
}
