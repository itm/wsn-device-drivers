package de.uniluebeck.itm.devicedriver.jennic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Programable;
import de.uniluebeck.itm.devicedriver.Sector;
import de.uniluebeck.itm.devicedriver.exception.FlashConfigurationFailedException;
import de.uniluebeck.itm.devicedriver.exception.FlashEraseFailedException;
import de.uniluebeck.itm.devicedriver.exception.FlashTypeReadFailedException;
import de.uniluebeck.itm.devicedriver.exception.InvalidChecksumException;
import de.uniluebeck.itm.devicedriver.exception.TimeoutException;
import de.uniluebeck.itm.devicedriver.exception.UnexpectedResponseException;
import de.uniluebeck.itm.devicedriver.generic.FlashType;
import de.uniluebeck.itm.devicedriver.jennic.exception.SectorEraseException;
import de.uniluebeck.itm.devicedriver.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.devicedriver.operation.EraseFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.devicedriver.operation.ProgramOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.devicedriver.operation.ResetOperation;
import de.uniluebeck.itm.devicedriver.operation.SendOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteMacAddressOperation;
import de.uniluebeck.itm.devicedriver.serialport.AbstractSerialPortDevice;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortEnterProgramModeOperation;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortLeaveProgramModeOperation;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortSendOperation;

public class JennicDevice extends AbstractSerialPortDevice implements Programable {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennicDevice.class);
	
	private static final int TIMEOUT = 2000;
	
	public JennicDevice(SerialPortConnection connection) {
		super(connection);
	}
	
	@Override
	public EnterProgramModeOperation createEnterProgramModeOperation() {
		return new SerialPortEnterProgramModeOperation(connection);
	}
	
	@Override
	public LeaveProgramModeOperation createLeaveProgramModeOperation() {
		return new SerialPortLeaveProgramModeOperation(connection);
	}
	
	@Override
	public EraseFlashOperation createEraseFlashOperation() {
		return new JennicEraseFlashOperation(this);
	}

	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		return new JennicGetChipTypeOperation(this);
	}
	
	public GetFlashHeaderOperation createGetFlashHeaderOperation() {
		return new JennicGetFlashHeaderOperation(this);
	}

	@Override
	public ProgramOperation createProgramOperation() {
		return new JennicProgramOperation(this);
	}

	@Override
	public ReadFlashOperation createReadFlashOperation() {
		return new JennicReadFlashOperation(this);
	}

	@Override
	public ReadMacAddressOperation createReadMacAddressOperation() {
		return new JennicReadMacAddressOperation(this);
	}

	@Override
	public ResetOperation createResetOperation() {
		return new JennicResetOperation(connection);
	}

	@Override
	public SendOperation createSendOperation() {
		return new SerialPortSendOperation(connection);
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		return new JennicWriteFlashOperation(this);
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		return new JennicWriteMacAddressOperation(this);
	}

	@Override
	public int[] getChannels() {
		return new int[] { 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };
	}
	
	public FlashType getFlashType() throws Exception {
		// Send flash type read request
		sendBootLoaderMessage(Messages.flashTypeReadRequestMessage());

		// Read flash type read response
		byte[] response = receiveBootLoaderReply(Messages.FLASH_TYPE_READ_RESPONSE);

		// Throw error if reading failed
		if (response[1] != 0x00) {
			log.error(String.format("Failed to read flash type: Response should be 0x00, yet it is: 0x%02x",
					response[1]));
			throw new FlashTypeReadFailedException();
		}

		// Determine flash type
		FlashType ft = FlashType.Unknown;
		if (response[2] == (byte) 0xBF && response[3] == (byte) 0x49)
			ft = FlashType.SST25VF010A;
		else if (response[2] == (byte) 0x10 && response[3] == (byte) 0x10)
			ft = FlashType.STM25P10A;
		else if (response[2] == (byte) 0x1F && response[3] == (byte) 0x60)
			ft = FlashType.Atmel25F512;
		else
			ft = FlashType.Unknown;

		// log.debug("Flash is " + ft + " (response[2,3] was: " + Tools.toHexString(response[2]) + " " +
		// Tools.toHexString(response[3]) + ")");
		return ft;
	}
	
	void enableFlashErase() throws Exception {
		// log.debug("Setting FLASH status register to zero");
		sendBootLoaderMessage(Messages.statusRegisterWriteMessage((byte) 0x00)); // see
		// AN
		// -
		// 1007

		byte[] response = receiveBootLoaderReply(Messages.WRITE_SR_RESPONSE);

		if (response[1] != 0x0) {
			log.error(String.format("Failed to write status register."));
			throw new FlashEraseFailedException();
		}
	}
	
	public void eraseFlash(Sector sector) throws Exception {
		enableFlashErase();
		log.debug("Erasing sector " + sector);
		sendBootLoaderMessage(Messages.sectorEraseRequestMessage(sector));

		byte[] response = receiveBootLoaderReply(Messages.SECTOR_ERASE_RESPONSE);

		if (response[1] != 0x0) {
			log.error(String.format("Failed to erase flash sector."));
			throw new SectorEraseException(sector);
		}

	}
	
	public void configureFlash(ChipType chipType) throws Exception {
		log.debug("Configuring flash");

		// Only new chips need to be configured
		if (chipType != ChipType.JN5121) {
			// Determine flash type
			FlashType flashType = getFlashType();

			// Send flash configure request
			sendBootLoaderMessage(Messages.flashConfigureRequestMessage(flashType));

			// Read flash configure response
			byte[] response = receiveBootLoaderReply(Messages.FLASH_CONFIGURE_RESPONSE);

			// Throw error if configuration failed
			if (response[1] != 0x00) {
				log.error(String.format("Failed to configure flash ROM: Response should be 0x00, yet it is: 0x%02x",
						response[1]));
				throw new FlashConfigurationFailedException();
			}
		}
		log.debug("Done. Flash is configured");
	}
	
	/** 
	 * 
	 */
	public void sendBootLoaderMessage(byte[] message) throws IOException {
		
		// Allocate buffer for length + message + checksum
		byte[] data = new byte[message.length + 2];

		// Prepend length (of message + checksum)
		data[0] = (byte) (message.length + 1);

		// Copy message into the buffer
		System.arraycopy(message, 0, data, 1, message.length);

		// Calculate and append checksum
		data[data.length - 1] = Messages.calculateChecksum(data, 0, data.length - 1);

		// Send message
		final OutputStream outStream = connection.getOutputStream();
		outStream.write(data);
		outStream.flush();
	}

	/** 
	 * 
	 */
	public byte[] receiveBootLoaderReply(int type) throws TimeoutException, UnexpectedResponseException, InvalidChecksumException, IOException, NullPointerException {
		InputStream inputStream = connection.getInputStream();
		
		waitDataAvailable(TIMEOUT);
		// Read message length
		int length = (int) inputStream.read();

		// Allocate message buffer
		byte[] message = new byte[length - 1];

		// Read rest of the message (except the checksum
		for (int i = 0; i < message.length; ++i) {
			waitDataAvailable(TIMEOUT);
			message[i] = (byte) inputStream.read();
		}

		// log.debug("Received boot loader msg: " + Tools.toHexString(message));

		// Read checksum
		waitDataAvailable(TIMEOUT);
		byte recvChecksum = (byte) inputStream.read();

		// Concatenate length and message for checksum calculation
		byte[] fullMessage = new byte[message.length + 1];
		fullMessage[0] = (byte) length;
		System.arraycopy(message, 0, fullMessage, 1, message.length);

		// Throw exception if checksums diffe
		byte checksum = Messages.calculateChecksum(fullMessage);
		if (checksum != recvChecksum) {
			throw new InvalidChecksumException();
		}
		// Check if the response type is unexpected
		if (message[0] != type) {
			throw new UnexpectedResponseException(type, message[0]);
		}
		return message;
	}
	
	/** 
	 * 
	 */
	public boolean waitForConnection() {
		try {
			// Send flash read request (in fact, this could be any valid message
			// to which the
			// device is supposed to respond)
			sendBootLoaderMessage(Messages.flashReadRequestMessage(0x24, 0x20));
			receiveBootLoaderReply(Messages.FLASH_READ_RESPONSE);
			log.info("Device connection established");
			return true;
		} catch (TimeoutException to) {
			log.debug("Still waiting for a connection.");
		} catch (Exception error) {
			log.warn("Exception while waiting for connection", error);
		}

		connection.flush();
		return false;
	}
}
