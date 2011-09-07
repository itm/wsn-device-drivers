package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;

public class JennicReadMacAddressOperation implements ReadMacAddressOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicReadMacAddressOperation.class);
	
	private final Provider<GetChipTypeOperation> getChipTypeProvider;
	
	private final Provider<ReadFlashOperation> readFlashProvider;
	
	@Inject
	public JennicReadMacAddressOperation(Provider<GetChipTypeOperation> getChipTypeProvider,
			Provider<ReadFlashOperation> readFlashProvider) {
		this.getChipTypeProvider = getChipTypeProvider;
		this.readFlashProvider = readFlashProvider;
	}
	
	@Override
	public MacAddress run(ProgressManager progressManager, OperationContext context) throws Exception {
		log.trace("Reading MAC Adress");
		// Connection established, determine chip type
		final ChipType chipType = context.run(getChipTypeProvider.get(), progressManager, 0.8f);
		log.trace("Chip type is " + chipType);

		// Connection established, read flash header
		final int address = chipType.getMacInFlashStart();
		final ReadFlashOperation readFlashOperation = readFlashProvider.get();
		readFlashOperation.setAddress(address, 8);
		final byte[] header = context.run(readFlashOperation, progressManager, 0.2f);

		final MacAddress macAddress = new MacAddress(header);
		log.trace("Done, result is: " + macAddress);
		return macAddress;
	}

}
