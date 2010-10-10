package de.uniluebeck.itm.devicedriver.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractReadMacAddressOperation;
import de.uniluebeck.itm.devicedriver.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadFlashOperation;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;

public class JennicReadMacAddressOperation extends AbstractReadMacAddressOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicReadMacAddressOperation.class);
	
	private final SerialPortConnection connection;
	
	private final JennicDevice device;
	
	public JennicReadMacAddressOperation(JennicDevice device) {
		this.device = device;
		connection = (SerialPortConnection) device.getConnection();
	}
	
	@Override
	public MacAddress execute(Monitor monitor) throws Exception {
		log.debug("Reading MAC Adress");
		// Enter programming mode
		EnterProgramModeOperation enterProgramModeOperation = device.createEnterProgramModeOperation();
		executeSubOperation(enterProgramModeOperation);

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
		
		final LeaveProgramModeOperation leaveProgramModeOperation = device.createLeaveProgramModeOperation();
		executeSubOperation(leaveProgramModeOperation);
		
		return macAddress;
	}

}
