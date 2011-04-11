package de.uniluebeck.itm.rsc.drivers.telosb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.rsc.drivers.core.exception.FlashEraseFailedException;
import de.uniluebeck.itm.rsc.drivers.core.exception.FlashProgramFailedException;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractProgressManager;
import de.uniluebeck.itm.rsc.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.rsc.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.rsc.drivers.core.serialport.SerialPortConnection.SerialPortMode;

public class TelosbEnterProgramModeOperation extends AbstractOperation<Void> implements EnterProgramModeOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(TelosbEnterProgramModeOperation.class);

	private final BSLTelosb bsl;
	
	private final SerialPortConnection connection;
	
	public TelosbEnterProgramModeOperation(SerialPortConnection connection, BSLTelosb bsl) {
		this.connection = connection;
		this.bsl = bsl;
	}
	
	@Override
	public Void execute(AbstractProgressManager progressManager) throws Exception {
		connection.setSerialPortMode(SerialPortMode.PROGRAM);
		
		byte[] reply;
		int bslVersion;
		int deviceId;
		String replyString = "";

		// invoke boot loader
		log.trace("Starting boot loader...");
		if (!bsl.invokeBSL()) {
			throw new FlashProgramFailedException("Failed to start boot loader.");
		}

		// perform mass erase to reset the password to default password
		log.trace("Erasing flash memory...");
		bsl.sendBSLCommand(BSLTelosb.CMD_MASSERASE, 0xFFFF, 0xA506, null, false);
		reply = bsl.receiveBSLReply();
		log.trace("Erasing flash memory...3");
		if ((reply[0] & 0xff) == BSLTelosb.DATA_NACK) {
			throw new FlashEraseFailedException("Failed to perform mass erase, NACK received.");
		} else if (reply.length > 1) {
			throw new FlashEraseFailedException("Failed to perform mass erase, reply length unexpected.");
		}

		// send default password
		log.info("Transmitting password...");
		if (!bsl.transmitPassword(null, false)) {
			throw new FlashProgramFailedException("Failed to transmit password, received NACK.");
		}

		// read boot loader version
		log.trace("Reading BSL version...");
		bsl.sendBSLCommand(BSLTelosb.CMD_RXBSLVERSION, 0, 0, null, false);
		reply = bsl.receiveBSLReply();

		if (reply.length != 16) {
			for (int i = 0; i < reply.length; i++) {
				replyString += String.format(" 0x%02x ", reply[i]);
			}
			throw new FlashProgramFailedException("Unable to read BSL version, reply length is unexpected: " + replyString);
		}

		deviceId = (((reply[0] & 0xFF) << 8) | (reply[1] & 0xFF));
		bslVersion = (((reply[10] & 0xFF) << 8) | (reply[11] & 0xFF));

		log.debug(String.format(
				"Current bsl version: %02x.%02x, device id: 0x%04x",
				((bslVersion >> 8) & 0xFF), (bslVersion & 0xFF), deviceId
		));

		// check if patch is required
		if (bslVersion <= 0x0110) {
			throw new FlashProgramFailedException("Current BSL version is 1.1 or below, patch is required");
		}

		// change baudrate to 38000
		if (bslVersion >= 0x0160 && !bsl.changeBaudrate(BSLTelosb.BaudRate.Baud38000)) {
			log.warn("Could not change the baud rate, keeping initial baud rate of 9600.");
		}
		return null;
	}
}
