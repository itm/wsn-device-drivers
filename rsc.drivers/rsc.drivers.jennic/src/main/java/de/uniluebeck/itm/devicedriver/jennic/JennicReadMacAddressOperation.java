package de.uniluebeck.itm.devicedriver.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadMacAddressOperation;

public class JennicReadMacAddressOperation extends AbstractOperation<MacAddress>implements ReadMacAddressOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicReadMacAddressOperation.class);
	
	private final JennicDevice device;
	
	public JennicReadMacAddressOperation(JennicDevice device) {
		this.device = device;
	}
	
	@Override
	public MacAddress execute(final Monitor monitor) throws Exception {
		log.debug("Reading MAC Adress");
		// Connection established, determine chip type
		final GetChipTypeOperation getChipTypeOperation = device.createGetChipTypeOperation();
		final ChipType chipType = executeSubOperation(getChipTypeOperation, monitor);
		log.debug("Chip type is " + chipType);

		// Connection established, read flash header
		final int address = chipType.getMacInFlashStart();
		final ReadFlashOperation readFlashOperation = device.createReadFlashOperation();
		readFlashOperation.setAddress(address, 8);
		final byte[] header = executeSubOperation(readFlashOperation, monitor);

		final MacAddress macAddress = new MacAddress(header);
		log.debug("Done, result is: " + macAddress);
		return macAddress;
	}

}