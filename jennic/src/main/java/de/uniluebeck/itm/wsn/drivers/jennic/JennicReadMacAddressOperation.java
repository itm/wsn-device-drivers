package de.uniluebeck.itm.wsn.drivers.jennic;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationFactory;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class JennicReadMacAddressOperation extends TimeLimitedOperation<MacAddress> implements ReadMacAddressOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicReadMacAddressOperation.class);

	private final OperationFactory operationFactory;

	@Inject
	public JennicReadMacAddressOperation(final TimeLimiter timeLimiter,
										 final OperationFactory operationFactory,
										 @Assisted final long timeoutMillis,
										 @Assisted @Nullable final OperationListener<MacAddress> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.operationFactory = operationFactory;
	}

	@Override
	@SerialPortProgrammingMode
	protected MacAddress callInternal() throws Exception {

		log.trace("Reading MAC address...");

		// Connection established, determine chip type
		final ChipType chipType = runSubOperation(operationFactory.createGetChipTypeOperation(1000, null), 0.8f);
		log.trace("Chip type is {}", chipType);

		// Connection established, read flash header
		final int address = chipType.getMacInFlashStart();
		final byte[] header = runSubOperation(operationFactory.createReadFlashOperation(address, 8, 120000, null), 0.2f);

		final MacAddress macAddress = new MacAddress(header);
		log.trace("Done reading MAC address {}", macAddress);

		return macAddress;
	}
}
