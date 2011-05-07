package de.uniluebeck.itm.wsn.drivers.telosb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.wsn.drivers.core.exception.FlashEraseFailedException;
import de.uniluebeck.itm.wsn.drivers.core.exception.FlashProgramFailedException;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection.SerialPortMode;

public class TelosbEnterProgramModeOperation extends AbstractOperation<Void> implements EnterProgramModeOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(TelosbEnterProgramModeOperation.class);

	private final BSLTelosb bsl;
	
	private final SerialPortConnection connection;
	
	public TelosbEnterProgramModeOperation(final SerialPortConnection connection, final BSLTelosb bsl) {
		this.connection = connection;
		this.bsl = bsl;
	}
	
	private void startBootLoader() throws FlashProgramFailedException {
		LOG.trace("Starting boot loader...");
		if (!bsl.invokeBSL()) {
			throw new FlashProgramFailedException("Failed to start boot loader.");
		}
	}
	
	private void resetPassword() throws Exception {
		LOG.trace("Erasing flash memory...");
		bsl.sendBSLCommand(BSLTelosb.CMD_MASSERASE, 0xFFFF, 0xA506, null, false);
		final byte[] reply = bsl.receiveBSLReply();
		if ((reply[0] & 0xff) == BSLTelosb.DATA_NACK) {
			throw new FlashEraseFailedException("Failed to perform mass erase, NACK received.");
		} else if (reply.length > 1) {
			throw new FlashEraseFailedException("Failed to perform mass erase, reply length unexpected.");
		}
		
		// send default password
		LOG.trace("Transmitting password...");
		if (!bsl.transmitPassword(null, false)) {
			throw new FlashProgramFailedException("Failed to transmit password, received NACK.");
		}
	}
	
	private int readBSLVersion() throws Exception {
		LOG.trace("Reading BSL version...");
		bsl.sendBSLCommand(BSLTelosb.CMD_RXBSLVERSION, 0, 0, null, false);
		byte[] reply = bsl.receiveBSLReply();
		if (reply.length != 16) {
			String replyString = "";
			for (int i = 0; i < reply.length; i++) {
				replyString += String.format(" 0x%02x ", reply[i]);
			}
			throw new FlashProgramFailedException("Unable to read BSL version, reply length is unexpected: " + replyString);
		}

		int deviceId = (((reply[0] & 0xFF) << 8) | (reply[1] & 0xFF));
		int bslVersion = (((reply[10] & 0xFF) << 8) | (reply[11] & 0xFF));
		
		LOG.trace(String.format("Current bsl version: %02x.%02x, device id: 0x%04x", ((bslVersion >> 8) & 0xFF), (bslVersion & 0xFF), deviceId));
		return bslVersion;
	}
	
	@Override
	public Void execute(ProgressManager progressManager) throws Exception {
		connection.setSerialPortMode(SerialPortMode.PROGRAM);
		progressManager.worked(0.2f);

		// invoke boot loader
		startBootLoader();
		progressManager.worked(0.2f);

		// perform mass erase to reset the password to default password
		resetPassword();
		progressManager.worked(0.2f);

		// read boot loader version
		int bslVersion = readBSLVersion();
		// check if patch is required
		if (bslVersion <= 0x0110) {
			throw new FlashProgramFailedException("Current BSL version is 1.1 or below, patch is required");
		}
		progressManager.worked(0.2f);

		// change baudrate to 38000
		if (bslVersion >= 0x0160 && !bsl.changeBaudrate(BSLTelosb.BaudRate.Baud38000)) {
			LOG.warn("Could not change the baud rate, keeping initial baud rate of 9600.");
		}
		return null;
	}
}
