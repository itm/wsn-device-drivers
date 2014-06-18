package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.exception.FlashProgramFailedException;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationFactory;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import de.uniluebeck.itm.wsn.drivers.core.util.BinaryImageBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;

public class TelosbProgramOperation extends AbstractProgramOperation {

	private static final Logger log = LoggerFactory.getLogger(TelosbProgramOperation.class);

	private final BSLTelosb bsl;

	private final OperationFactory operationFactory;

	private final SerialPortConnection connection;

	@Inject
	public TelosbProgramOperation(final TimeLimiter timeLimiter,
								  final BSLTelosb bsl,
								  final OperationFactory operationFactory,
								  final SerialPortConnection connection,
								  @Assisted byte[] binaryImage,
								  @Assisted final long timeoutMillis,
								  @Assisted @Nullable final OperationListener<Void> operationCallback) {
		super(timeLimiter, binaryImage, timeoutMillis, operationCallback);
		this.bsl = bsl;
		this.operationFactory = operationFactory;
		this.connection = connection;
	}

	@Override
	@SerialPortProgrammingMode
	protected Void callInternal() throws Exception {

		final TelosbBinData binData = new TelosbBinData(getBinaryImage());

		log.trace("Starting to write program into flash memory...");

		final float workedFraction = 1f / binData.getBlockCount();
		int bytesProgrammed = 0;
		int blocksWritten = 0;

		for (BinaryImageBlock block = binData.getNextBlock(); block != null; block = binData.getNextBlock()) {

			final byte[] data = block.getData();
			final int address = block.getAddress();

			// write single block
			try {
				bsl.writeFlash(address, data, data.length);
			} catch (FlashProgramFailedException e) {
				final String msg = String.format(
						"Error writing %d bytes into flash at address 0x%02x: %s. Programmed %d bytes so far. ",
						data.length, address,
						e.getMessage(), bytesProgrammed
				);
				log.error(msg, e);
				throw e;
			} catch (final IOException e) {
				log.error("I/O error while writing flash. Programmed " + bytesProgrammed + " bytes so far.", e);
				throw e;
			}

			bytesProgrammed += data.length;
			blocksWritten++;

			log.trace("Programmed {}/{} blocks", blocksWritten, binData.getBlockCount());

			progress(workedFraction * blocksWritten);
		}

		return null;
	}
}
