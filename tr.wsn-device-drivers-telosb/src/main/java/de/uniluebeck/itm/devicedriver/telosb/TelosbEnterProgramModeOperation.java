package de.uniluebeck.itm.devicedriver.telosb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection.SerialPortMode;

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
	public Void execute(Monitor monitor) throws Exception {
		connection.setSerialPortMode(SerialPortMode.PROGRAM);
		
		byte[] reply;
		int bslVersion;
		int deviceId;
		String replyString = "";

		// invoke boot loader
		log.info("Starting boot loader...");
		if (!bsl.invokeBSL()) {
			log.debug("Failed to start boot loader.");
			return null;
		}

		// perform mass erase to reset the password to default password
		log.info("Erasing flash memory...");
		bsl.sendBSLCommand(BSLTelosb.CMD_MASSERASE, 0xFFFF, 0xA506, null, false);
		reply = bsl.receiveBSLReply();

		if ((reply[0] & 0xff) == BSLTelosb.DATA_NACK) {
			log.error("Failed to perform mass erase, NACK received.");
			return null;
		} else if (reply.length > 1) {
			log.error("Failed to perform mass erase, reply length unexpected.");
			return null;
		}

		// send default password
		log.info("Transmitting password...");
		if (!bsl.transmitPassword(null, false)) {
			log.error("Failed to transmit password, received NACK.");
			return null;
		}

		// read boot loader version
		if (log.isDebugEnabled()) {
			log.debug("Reading BSL version...");
		}
		bsl.sendBSLCommand(BSLTelosb.CMD_RXBSLVERSION, 0, 0, null, false);
		reply = bsl.receiveBSLReply();

		if (reply.length != 16) {
			for (int i = 0; i < reply.length; i++) {
				replyString += String.format(" 0x%02x ", reply[i]);
			}
			log.error("Unable to read BSL version, reply length is unexpected: " + replyString);
			return null;
		}

		deviceId = (((reply[0] & 0xFF) << 8) | (reply[1] & 0xFF));
		bslVersion = (((reply[10] & 0xFF) << 8) | (reply[11] & 0xFF));

		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"Current bsl version: %02x.%02x, device id: 0x%04x",
					((bslVersion >> 8) & 0xFF), (bslVersion & 0xFF), deviceId));
		}

		// check if patch is required
		if (bslVersion <= 0x0110) {
			log.error("Current BSL version is 1.1 or below, patch is required");
			return null;
		}

		// change baudrate to 38000
		if (bslVersion >= 0x0160) {
			if (!bsl.changeBaudrate(BSLTelosb.BaudRate.Baud38000)) {
				log.warn("Could not change the baud rate, keeping initial baud rate of 9600.");
			}
		}		
		return null;
	}
}
