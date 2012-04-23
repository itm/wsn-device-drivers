package de.uniluebeck.itm.wsn.drivers.jennic;

import com.google.inject.Inject;
import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.exception.*;
import de.uniluebeck.itm.wsn.drivers.isense.exception.FlashTypeReadFailedException;
import de.uniluebeck.itm.wsn.drivers.jennic.exception.SectorEraseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static de.uniluebeck.itm.tr.util.StringUtils.toHexString;

public class JennicHelper {

	private static final Logger log = LoggerFactory.getLogger(JennicHelper.class);

	private static final int TIMEOUT_WAIT_DATA_AVAILABLE_MILLIS = 2500;

	private final Connection connection;

	@Inject
	public JennicHelper(Connection connection) {
		this.connection = connection;
	}

	public FlashType getFlashType() throws Exception {
		// Send flash type read request
		sendBootloaderMessage(Messages.flashTypeReadRequestMessage());

		// Read flash type read response
		byte[] response = receiveBootloaderReply(Messages.FLASH_TYPE_READ_RESPONSE);

		// Throw error if reading failed
		if (response[1] != 0x00) {
			log.error(String.format("Failed to read flash type: Response should be 0x00, yet it is: 0x%02x",
					response[1]
			)
			);
			throw new FlashTypeReadFailedException();
		}

		// Determine flash type
		if (response[2] == (byte) 0xBF && response[3] == (byte) 0x49) {
			return FlashType.SST25VF010A;
		} else if (response[2] == (byte) 0x10 && response[3] == (byte) 0x10) {
			return FlashType.STM25P10A;
		} else if (response[2] == (byte) 0x1F && response[3] == (byte) 0x60) {
			return FlashType.Atmel25F512;
		} else if (response[2] == (byte) 0x12 && response[3] == (byte) 0x12) {
			return FlashType.STM25P40;
		} else {
			return FlashType.Unknown;
		}
	}

	void enableFlashErase() throws Exception {
		// log.debug("Setting FLASH status register to zero");
		sendBootloaderMessage(Messages.statusRegisterWriteMessage((byte) 0x00)); // see
		// AN
		// -
		// 1007

		byte[] response = receiveBootloaderReply(Messages.WRITE_SR_RESPONSE);

		if (response[1] != 0x0) {
			log.error(String.format("Failed to write status register."));
			throw new FlashEraseFailedException();
		}
	}

	public void eraseFlash(Sector sector) throws Exception {
		enableFlashErase();
		log.trace("Erasing sector " + sector);
		sendBootloaderMessage(Messages.sectorEraseRequestMessage(sector));

		byte[] response = receiveBootloaderReply(Messages.SECTOR_ERASE_RESPONSE);

		if (response[1] != 0x0) {
			log.error(String.format("Failed to erase flash sector."));
			throw new SectorEraseException(sector);
		}

	}

	public void configureFlash(ChipType chipType) throws Exception {

		log.trace("Configuring flash");

		// only new chips need to be configured
		if (chipType != ChipType.JN5121) {

			// determine flash type
			FlashType flashType = getFlashType();

			// send flash configure request
			sendBootloaderMessage(Messages.flashConfigureRequestMessage(flashType));

			// read flash configure response
			byte[] response = receiveBootloaderReply(Messages.FLASH_CONFIGURE_RESPONSE);

			// throw error if configuration failed
			if (response[1] != 0x00) {
				if (log.isErrorEnabled()) {
					log.error("Failed to configure flash ROM: response should be 0x00, yet it is: ",
							toHexString(response[1])
					);
				}
				throw new FlashConfigurationFailedException();
			}
		}

		log.trace("Done. Flash is configured.");
	}

	public void sendBootloaderMessage(byte[] message) throws IOException {

		if (log.isTraceEnabled()) {
			log.trace("Sending bootloader request: {}", StringUtils.toHexString(message));
		}

		// allocate buffer for length + message + checksum
		byte[] data = new byte[message.length + 2];

		// prepend length (of message + checksum)
		data[0] = (byte) (message.length + 1);

		// copy message into the buffer
		System.arraycopy(message, 0, data, 1, message.length);

		// calculate and append checksum
		data[data.length - 1] = Messages.calculateChecksum(data, 0, data.length - 1);

		// send message
		final OutputStream outStream = connection.getOutputStream();
		outStream.write(data);
		outStream.flush();
	}

	public byte[] receiveBootloaderReply(int expectedType)
			throws TimeoutException, UnexpectedResponseException, InvalidChecksumException, IOException,
			NullPointerException {

		final InputStream inputStream = connection.getInputStream();

		connection.waitDataAvailable(TIMEOUT_WAIT_DATA_AVAILABLE_MILLIS);

		int bootLoaderReplyLength = inputStream.read();
		byte[] bootLoaderReply = new byte[bootLoaderReplyLength - 1];

		// read rest of the reply (except of the checksum)
		for (int i = 0; i < (bootLoaderReplyLength - 1); ++i) {
			connection.waitDataAvailable(TIMEOUT_WAIT_DATA_AVAILABLE_MILLIS);
			bootLoaderReply[i] = (byte) inputStream.read();
		}

		if (log.isTraceEnabled()) {
			log.trace("Received bootloader reply: {}", StringUtils.toHexString(bootLoaderReply));
		}

		// read checksum
		connection.waitDataAvailable(TIMEOUT_WAIT_DATA_AVAILABLE_MILLIS);
		byte checksumReceived = (byte) inputStream.read();

		if (log.isTraceEnabled()) {
			log.trace("Received bootloader reply checksum: {}", toHexString(checksumReceived));
		}

		// concatenate length field and actual reply for checksum calculation
		byte[] fullBootLoaderReply = new byte[bootLoaderReply.length + 1];
		fullBootLoaderReply[0] = (byte) bootLoaderReplyLength;
		System.arraycopy(bootLoaderReply, 0, fullBootLoaderReply, 1, bootLoaderReply.length);

		// throw exception if checksums differ
		byte checksumCalculated = Messages.calculateChecksum(fullBootLoaderReply);
		if (checksumCalculated != checksumReceived) {
			String msg = "Bootloader reply checksum mismatch (received " + toHexString(checksumReceived) +
					", calculated" + toHexString(checksumCalculated) + ")";
			throw new InvalidChecksumException(msg);
		}

		// check if the response type is unexpected
		if (bootLoaderReply[0] != expectedType) {
			throw new UnexpectedResponseException(expectedType, (int) bootLoaderReply[0]);
		}

		return bootLoaderReply;
	}

	public boolean waitForConnection() {

		try {

			// send flash read request (in fact, this could be any message to which the device is supposed to respond)
			sendBootloaderMessage(Messages.flashReadRequestMessage(0x24, 0x20));
			receiveBootloaderReply(Messages.FLASH_READ_RESPONSE);
			log.trace("Device connection established");
			return true;

		} catch (TimeoutException e) {
			try {
				connection.clear();
			} catch (IOException e1) {
				log.error("Exception while cleaning the stream.", e1);
			}
			log.trace("waitForConnection timed out!");
			return false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] readFlash(int address, int len) throws Exception {

		// Send flash program request
		sendBootloaderMessage(Messages.flashReadRequestMessage(address, len));

		// Read flash program response
		byte[] response = receiveBootloaderReply(Messages.FLASH_READ_RESPONSE);

		// Remove type and success octet
		byte[] data = new byte[response.length - 2];
		System.arraycopy(response, 2, data, 0, response.length - 2);

		// Return data
		return data;
	}

	public void writeFlash(int address, byte[] data)
			throws IOException, NullPointerException, TimeoutException, UnexpectedResponseException,
			InvalidChecksumException, FlashProgramFailedException {
		// Send flash program request
		// log.debug("Sending program request for address " + address + " with " + data.length + " bytes");
		sendBootloaderMessage(Messages.flashProgramRequestMessage(address, data));

		// Read flash program response
		byte[] response = receiveBootloaderReply(Messages.FLASH_PROGRAM_RESPONSE);

		// Throw error if writing failed
		if (response[1] != 0x0) {
			log.error(
					String.format("Failed to write to flash: Response should be 0x00, yet it is: 0x%02x", response[1])
			);
			throw new FlashProgramFailedException();
		}
	}
}
