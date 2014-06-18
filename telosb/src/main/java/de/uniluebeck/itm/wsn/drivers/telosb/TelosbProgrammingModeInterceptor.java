package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.exception.FlashEraseFailedException;
import de.uniluebeck.itm.wsn.drivers.core.exception.FlashProgramFailedException;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection.SerialPortMode;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingModeInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TelosbProgrammingModeInterceptor extends SerialPortProgrammingModeInterceptor {

	private static final Logger LOG = LoggerFactory.getLogger(TelosbProgrammingModeInterceptor.class);

	@Inject
	private BSLTelosb bsl;

	@Inject
	private SerialPortConnection connection;

	@Override
	public void enterProgrammingMode() throws Exception {

		connection.setSerialPortMode(SerialPortMode.PROGRAM);

		// invoke boot loader
		startBootLoader(bsl);

		// perform mass erase to reset the password to default password
		resetPassword(bsl);

		// read boot loader version
		int bslVersion = readBSLVersion(bsl);
		// check if patch is required
		if (bslVersion <= 0x0110) {
			throw new FlashProgramFailedException("Current BSL version is 1.1 or below, patch is required");
		}

		// change baud rate to 38000
		if (bslVersion >= 0x0160 && !bsl.changeBaudRate(BSLTelosb.BaudRate.Baud38000)) {
			LOG.warn("Could not change the baud rate, keeping initial baud rate of 9600.");
		}
	}

	@Override
	public void leaveProgrammingMode() throws Exception {
		LOG.trace("Leaving programming mode...");
		connection.setSerialPortMode(SerialPortMode.NORMAL);
		LOG.trace("Programming mode left");
	}

	private void startBootLoader(BSLTelosb bsl) throws FlashProgramFailedException {
		LOG.trace("Starting boot loader...");
		if (!bsl.invokeBSL()) {
			throw new FlashProgramFailedException("Failed to start boot loader.");
		}
	}

	private void resetPassword(BSLTelosb bsl) throws Exception {

		LOG.trace("Erasing flash memory...");

		bsl.sendBSLCommand(BSLTelosb.CMD_MASSERASE, 0xFF00, 0xA506, null, false);

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

	private int readBSLVersion(BSLTelosb bsl) throws Exception {

		LOG.trace("Reading BSL version...");

		bsl.sendBSLCommand(BSLTelosb.CMD_RXBSLVERSION, 0, 0, null, false);
		byte[] reply = bsl.receiveBSLReply();

		if (reply.length != 16) {

			String replyString = "";
			for (int i = 0; i < reply.length; i++) {
				replyString += String.format(" 0x%02x ", reply[i]);
			}
			throw new FlashProgramFailedException(
					"Unable to read BSL version, reply length is unexpected: " + replyString
			);
		}

		int deviceId = (((reply[0] & 0xFF) << 8) | (reply[1] & 0xFF));
		int bslVersion = (((reply[10] & 0xFF) << 8) | (reply[11] & 0xFF));

		if (LOG.isTraceEnabled()) {
			LOG.trace(String.format("Current bsl version: %02x.%02x, device id: 0x%04x",
					((bslVersion >> 8) & 0xFF),
					(bslVersion & 0xFF),
					deviceId
			)
			);
		}
		return bslVersion;
	}
}
