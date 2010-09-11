package de.uniluebeck.itm.devicedriver.jennec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.SerialPortConnection;
import de.uniluebeck.itm.devicedriver.jennec.exception.EnterProgramModeException;
import de.uniluebeck.itm.devicedriver.operation.AbstractReadMacAddressOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadFlashOperation;

public class JennecReadMacAddressOperation extends AbstractReadMacAddressOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennecReadMacAddressOperation.class);
	
	private final SerialPortConnection connection;
	
	private final JennecDevice device;
	
	public JennecReadMacAddressOperation(JennecDevice device) {
		this.device = device;
		connection = (SerialPortConnection) device.getConnection();
	}
	
	@Override
	public MacAddress execute(Monitor monitor) throws Exception {
		log.debug("Reading MAC Adress");
		// Enter programming mode
		EnterProgramModeOperation enterProgramModeOperation = device.createEnterProgramModeOperation();
		if (!executeSubOperation(enterProgramModeOperation)) {
			log.error("Unable to enter programming mode");
			throw new EnterProgramModeException("Unable to enter programming mode");
		}

		connection.flush();

		// Wait for a connection
		while (!isCanceled() && !device.waitForConnection())
			log.info("Still waiting for a connection");

		// Return with success if the user has requested to cancel this
		// operation
		if (isCanceled()) {
			log.debug("Operation has been cancelled");
			return null;
		}

		// Connection established, determine chip type
		final GetChipTypeOperation getChipTypeOperation = device.createGetChipTypeOperation();
		final ChipType chipType = executeSubOperation(getChipTypeOperation);
		log.debug("Chip type is " + chipType);

		// Connection established, read flash header
		final int address = chipType.getMacInFlashStart();
		final ReadFlashOperation readFlashOperation = device.createReadFlashOperation();
		readFlashOperation.setAddress(address, 8);
		final byte[] header = executeSubOperation(readFlashOperation);

		final MacAddress macAddress = new MacAddress(header);
		log.debug("Done, result is: " + macAddress);
		return macAddress;
	}

}
