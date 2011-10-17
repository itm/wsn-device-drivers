package de.uniluebeck.itm.wsn.drivers.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.Program;

public class JennicReadMacAddressOperation implements ReadMacAddressOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicReadMacAddressOperation.class);
	
	private final GetChipTypeOperation getChipTypeOperation;
	
	private final ReadFlashOperation readFlashOperation;
	
	@Inject
	public JennicReadMacAddressOperation(GetChipTypeOperation getChipTypeProvider, 
			ReadFlashOperation readFlashProvider) {
		this.getChipTypeOperation = getChipTypeProvider;
		this.readFlashOperation = readFlashProvider;
	}
	
	@Override
	@Program
	public MacAddress run(ProgressManager progressManager, OperationContext context) throws Exception {
		log.trace("Reading MAC Adress");
		// Connection established, determine chip type
		final ChipType chipType = context.run(getChipTypeOperation, progressManager, 0.8f);
		log.trace("Chip type is " + chipType);

		// Connection established, read flash header
		final int address = chipType.getMacInFlashStart();
		readFlashOperation.setAddress(address, 8);
		final byte[] header = context.run(readFlashOperation, progressManager, 0.2f);

		final MacAddress macAddress = new MacAddress(header);
		log.trace("Done, result is: " + macAddress);
		return macAddress;
	}

}
