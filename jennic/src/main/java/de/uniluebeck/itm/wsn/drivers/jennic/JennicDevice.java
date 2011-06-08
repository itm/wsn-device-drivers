package de.uniluebeck.itm.wsn.drivers.jennic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Flushables;

import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.Programable;
import de.uniluebeck.itm.wsn.drivers.core.exception.FlashConfigurationFailedException;
import de.uniluebeck.itm.wsn.drivers.core.exception.FlashEraseFailedException;
import de.uniluebeck.itm.wsn.drivers.core.exception.FlashProgramFailedException;
import de.uniluebeck.itm.wsn.drivers.core.exception.InvalidChecksumException;
import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import de.uniluebeck.itm.wsn.drivers.core.exception.UnexpectedResponseException;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.SendOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.AbstractSerialPortDevice;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortEnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortLeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortSendOperation;
import de.uniluebeck.itm.wsn.drivers.isense.FlashType;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseResetOperation;
import de.uniluebeck.itm.wsn.drivers.isense.exception.FlashTypeReadFailedException;
import de.uniluebeck.itm.wsn.drivers.jennic.exception.SectorEraseException;

public class JennicDevice extends AbstractSerialPortDevice implements Programable {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(JennicDevice.class);
	
	private static final int TIMEOUT = 2000;
	
	public JennicDevice(SerialPortConnection connection) {
		super(connection);
	}
	
	@Override
	public EnterProgramModeOperation createEnterProgramModeOperation() {
		return new SerialPortEnterProgramModeOperation(getConnection());
	}
	
	@Override
	public LeaveProgramModeOperation createLeaveProgramModeOperation() {
		return new SerialPortLeaveProgramModeOperation(getConnection());
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
		return new iSenseResetOperation(getConnection());
	}

	@Override
	public SendOperation createSendOperation() {
		return new SerialPortSendOperation(getConnection());
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
			LOG.error(String.format("Failed to read flash type: Response should be 0x00, yet it is: 0x%02x",
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
		else if (response[2] == (byte) 0x12 && response[3] == (byte) 0x12)
			ft = FlashType.STM25P40;
		else 
			ft = FlashType.Unknown;

		// LOG.debug("Flash is " + ft + " (response[2,3] was: " + Tools.toHexString(response[2]) + " " +
		// Tools.toHexString(response[3]) + ")");
		return ft;
	}
	
	void enableFlashErase() throws Exception {
		// LOG.debug("Setting FLASH status register to zero");
		sendBootLoaderMessage(Messages.statusRegisterWriteMessage((byte) 0x00)); // see
		// AN
		// -
		// 1007

		byte[] response = receiveBootLoaderReply(Messages.WRITE_SR_RESPONSE);

		if (response[1] != 0x0) {
			LOG.error(String.format("Failed to write status register."));
			throw new FlashEraseFailedException();
		}
	}
	
	public void eraseFlash(Sector sector) throws Exception {
		enableFlashErase();
		LOG.trace("Erasing sector " + sector);
		sendBootLoaderMessage(Messages.sectorEraseRequestMessage(sector));

		byte[] response = receiveBootLoaderReply(Messages.SECTOR_ERASE_RESPONSE);

		if (response[1] != 0x0) {
			LOG.error(String.format("Failed to erase flash sector."));
			throw new SectorEraseException(sector);
		}

	}
	
	public void configureFlash(ChipType chipType) throws Exception {
		LOG.trace("Configuring flash");

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
				LOG.error(String.format("Failed to configure flash ROM: Response should be 0x00, yet it is: 0x%02x",
						response[1]));
				throw new FlashConfigurationFailedException();
			}
		}
		LOG.trace("Done. Flash is configured");
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
		final OutputStream outStream = getConnection().getOutputStream();
		outStream.write(data);
		outStream.flush();
	}

	/** 
	 * 
	 */
	public byte[] receiveBootLoaderReply(int type) throws TimeoutException, UnexpectedResponseException, InvalidChecksumException, IOException, NullPointerException {
		LOG.trace("Receiving Boot Loader Reply...");
		final SerialPortConnection connection = getConnection();
		final InputStream inputStream = connection.getInputStream();
		
		connection.waitDataAvailable(TIMEOUT);
		// Read message length
		int length = (int) inputStream.read();
		LOG.trace("receiveBootLoaderReply length: " + length);

		// Allocate message buffer
		byte[] message = new byte[length - 1];

		// Read rest of the message (except the checksum
		for (int i = 0; i < message.length; ++i) {
			connection.waitDataAvailable(TIMEOUT);
			message[i] = (byte) inputStream.read();
		}
		LOG.trace("Received boot loader msg: " + StringUtils.toHexString(message));

		// Read checksum
		connection.waitDataAvailable(TIMEOUT);
		byte recvChecksum = (byte) inputStream.read();
		LOG.trace("Received Checksum: " + StringUtils.toHexString(recvChecksum));

		// Concatenate length and message for checksum calculation
		byte[] fullMessage = new byte[message.length + 1];
		fullMessage[0] = (byte) length;
		System.arraycopy(message, 0, fullMessage, 1, message.length);

		// Throw exception if checksums diffe
		byte checksum = Messages.calculateChecksum(fullMessage);
		if (checksum != recvChecksum) {
			String msg = "Received: " + StringUtils.toHexString(recvChecksum) + ", Calculated: " + StringUtils.toHexString(checksum);
			throw new InvalidChecksumException(msg);
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
			LOG.trace("Device connection established");
			return true;
		} catch (TimeoutException e) {
			LOG.warn("Still waiting for a connection.");
		} catch (Exception e) {
			LOG.error("Exception while waiting for connection", e);
		}

		Flushables.flushQuietly(getConnection());
		return false;
	}
	
	public byte[] readFlash(int address, int len) throws Exception {

		// Send flash program request
		sendBootLoaderMessage(Messages.flashReadRequestMessage(address, len));

		// Read flash program response
		byte[] response = receiveBootLoaderReply(Messages.FLASH_READ_RESPONSE);

		// Remove type and success octet
		byte[] data = new byte[response.length - 2];
		System.arraycopy(response, 2, data, 0, response.length - 2);

		// Return data
		return data;
	}
	
	public void writeFlash(int address, byte[] data) throws IOException, NullPointerException, TimeoutException, UnexpectedResponseException, InvalidChecksumException, FlashProgramFailedException {
		// Send flash program request
		// LOG.debug("Sending program request for address " + address + " with " + data.length + " bytes");
		sendBootLoaderMessage(Messages.flashProgramRequestMessage(address, data));

		// Read flash program response
		byte[] response = receiveBootLoaderReply(Messages.FLASH_PROGRAM_RESPONSE);

		// Throw error if writing failed
		if (response[1] != 0x0) {
			LOG.error(String.format("Failed to write to flash: Response should be 0x00, yet it is: 0x%02x", response[1]));
			throw new FlashProgramFailedException();
		}
	}
}
