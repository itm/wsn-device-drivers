package de.uniluebeck.itm.devicedriver.pacemate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.Programable;
import de.uniluebeck.itm.devicedriver.exception.InvalidChecksumException;
import de.uniluebeck.itm.devicedriver.exception.TimeoutException;
import de.uniluebeck.itm.devicedriver.exception.UnexpectedResponseException;
import de.uniluebeck.itm.devicedriver.operation.AbstractWriteMacAddressOperation;
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
import de.uniluebeck.itm.tr.util.StringUtils;

public class PacemateDevice extends AbstractSerialPortDevice implements Programable {

	/**
	 * This is the Start Address in the RAM to write data
	 */
	public static final long START_ADDRESS_IN_RAM = 1073742336;
	
	private static final Logger log = LoggerFactory.getLogger(PacemateDevice.class);
	
	private static final int TIMEOUT = 2000;

	public PacemateDevice(SerialPortConnection connection) {
		super(connection);
	}
	
	@Override
	public int[] getChannels() {
		return new int[] { 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
				24, 25, 26 };
	}
	
	@Override
	public EnterProgramModeOperation createEnterProgramModeOperation() {
		final EnterProgramModeOperation operation = new SerialPortEnterProgramModeOperation(connection);
		monitor.monitorState(operation);
		return operation;
	}
	
	@Override
	public LeaveProgramModeOperation createLeaveProgramModeOperation() {
		final LeaveProgramModeOperation operation = new SerialPortLeaveProgramModeOperation(connection);
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		final GetChipTypeOperation operation = new PacemateGetChipTypeOperation(this);
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public ProgramOperation createProgramOperation() {
		final ProgramOperation operation = new PacemateProgramOperation(this);
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public EraseFlashOperation createEraseFlashOperation() {
		final EraseFlashOperation operation = new PacemateEraseFlashOperation(this);
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		final WriteFlashOperation operation = new PacemateWriteFlashOperation(this);
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public ReadFlashOperation createReadFlashOperation() {
		final ReadFlashOperation operation = new PacemateReadFlashOperation(this);
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public ReadMacAddressOperation createReadMacAddressOperation() {
		final ReadMacAddressOperation operation = new PacemateReadMacAddressOperation(this);
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		final WriteMacAddressOperation operation = new AbstractWriteMacAddressOperation() {
			@Override
			public Void execute(Monitor monitor) throws Exception {
				log.warn("writeMacAddres is not implemented.");
				return null;
			}
		};
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public ResetOperation createResetOperation() {
		final ResetOperation operation = new PacemateResetOperation(connection);
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public SendOperation createSendOperation() {
		final SendOperation operation = new SerialPortSendOperation(connection);
		monitor.monitorState(operation);
		return operation;
	}
	
	public boolean autobaud() {
		try {
			sendBootLoaderMessage(Messages.AutoBaudRequestMessage());
			receiveBootLoaderReply(Messages.SYNCHRONIZED);
			sendBootLoaderMessage(Messages.AutoBaudRequest2Message());
			receiveBootLoaderReply(Messages.SYNCHRONIZED);
			sendBootLoaderMessage(Messages.AutoBaudRequest3Message());
			receiveBootLoaderReply(Messages.OK);
			log.info("Autobaud");
		} catch (TimeoutException e) {
			log.debug("Still waiting for a connection.", e);
		} catch (Exception e) {
			log.warn("Exception while waiting for connection", e);
		}
		return true;
	}
	
	public void clearStreamData() {

		// Allocate message buffer max 255 bytes to read
		byte[] message = new byte[255];

		int index = 0;

		// Read the data
		boolean a = true;
		while ((a == true) && (index < 255)) {
			try {
				message[index] = (byte) connection.getInputStream().read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (message[index] == -1)
				a = false;
			else
				index++;
		}
	}
	
	public void configureFlash() throws Exception {
		// log.debug("Configuring flash");

		enableFlashErase();

		// Send flash configure request
		sendBootLoaderMessage(Messages.flashConfigureRequestMessage(3, 14));

		// Read flash configure response
		receiveBootLoaderReply(Messages.CMD_SUCCESS);

		// log.debug("Done. Flash is configured");
	}
	
	public void configureFlash(int start, int end) throws Exception {
		// log.debug("Configuring flash");

		enableFlashErase();

		// Send flash configure request
		sendBootLoaderMessage(Messages.flashConfigureRequestMessage(start, end));

		// Read flash configure response
		receiveBootLoaderReply(Messages.CMD_SUCCESS);

		// log.debug("Done. Flash is configured");
	}
	
	/**
	 * Pacemate style
	 */
	public byte[] copyRAMToFlash(long flashAddress, long ramAddress, int len) throws Exception {
		// Send flash program request
		// log.debug("Sending program request for address " + address + " with " + data.length + " bytes");
		sendBootLoaderMessage(Messages.copyRAMToFlashRequestMessage(flashAddress, ramAddress, len));

		// Read flash program response
		byte[] response = receiveBootLoaderReply(Messages.CMD_SUCCESS);

		// log.debug("Copy Ram to Flash ok");

		return response;
	}
	
	public void eraseFlash(int start, int end) throws Exception {
		// enableFlashErase();
		log.debug("Erasing sector "/* + sector*/);
		sendBootLoaderMessage(Messages.flashEraseRequestMessage(start, end));

		receiveBootLoaderReply(Messages.CMD_SUCCESS);
		try {
			receiveBootLoaderReply(Messages.CMD_SUCCESS);
		} catch (TimeoutException e) {
			log.debug("one line erase response");
		}
	}
	
	public void enableFlashErase() throws Exception {
		// log.debug("Erase Flash");
		sendBootLoaderMessage(Messages.Unlock_RequestMessage());

		receiveBootLoaderReply(Messages.CMD_SUCCESS);
	}

	public byte[] receiveBootLoaderReply(String type) throws TimeoutException, UnexpectedResponseException, InvalidChecksumException, IOException, NullPointerException {

		// Allocate message buffer max 255 bytes to read
		byte[] message = new byte[255];

		int index = 0;

		String returnCode = "";

		int response_data_start = 0;
		
		InputStream inStream = connection.getInputStream();

		waitDataAvailable(TIMEOUT);

		int read_returnCode = 1;

		// Read the message
		while (index < 255) {
			message[index] = (byte) inStream.read();
			if (message[index] == -1)
				break;
			if (read_returnCode == 2) {
				if (message[index] == 0x0d) {
					read_returnCode = 3;
					response_data_start = index + 2;
				} else
					returnCode = returnCode + (char) message[index];
			}
			if ((message[index] == 0x0a) && (read_returnCode == 1))
				read_returnCode = 2;
			index++;
		}

		// copy to real length
		byte[] fullMessage = new byte[index];
		System.arraycopy(message, 0, fullMessage, 0, index);

		log.debug("Received boot loader msg: "
				+ StringUtils.toHexString(fullMessage));

		if (returnCode.length() == 0)
			return message;

		if (type == Messages.DATA_ECHO)
			return message;

		// log.debug("Received boot loader msg: " + returnCode +" "+
		// Messages.getErrorMessage(returnCodeInt));

		if ((type.compareTo(Messages.DATA) == 0)
				&& (returnCode.compareTo(Messages.CMD_SUCCESS) == 0)) {
			byte[] dataMessage = new byte[index - response_data_start];
			System.arraycopy(message, response_data_start, dataMessage, 0,
					index - response_data_start);
			return dataMessage;
		}

		// Check if the response type is unexpected
		if (type.compareTo(Messages.CMD_SUCCESS) == 0) {
			if (returnCode.compareTo(Messages.CMD_SUCCESS) == 0)
				return message;
			else {
				log.debug("Received boot loader msg: " + returnCode);
				throw new UnexpectedResponseException(
						new Integer(type).intValue(),
						new Integer(returnCode).intValue());
			}
		} else if ((type.compareTo(Messages.SYNCHRONIZED) == 0)
				&& (message[0] == 0x53))
			return message;

		else if ((type.compareTo(Messages.SYNCHRONIZED_OK) == 0))
			return message;

		else if (type.compareTo(Messages.OK) == 0) {
			if (returnCode.compareTo(Messages.OK) == 0)
				return message;
			else {
				log.debug("Received boot loader msg: " + returnCode);
				throw new InvalidChecksumException("Invalid checksum - resend");
			}
		} else if (type.compareTo(Messages.ECHO_RESPONSE) == 0)
			return message;

		return message;
	}

	public void sendBootLoaderMessage(byte[] message) throws IOException {
		// Allocate buffer for message + CR and LF
		byte[] data = new byte[message.length + 2];

		// Copy message into the buffer
		System.arraycopy(message, 0, data, 0, message.length);

		// add CR and LF
		data[data.length - 2] = 0x0D; // <CR>
		data[data.length - 1] = 0x0A; // <LF>

		// Print message
		// log.debug("Sending boot loader msg: " + Tools.toHexString(data));

		// Send message
		OutputStream stream = connection.getOutputStream();
		stream.write(data);
		stream.flush();
	}
	
	/**
	 * writes the byte array to the out stream pacemate style
	 * 
	 * @throws NullPointerException
	 * @throws InvalidChecksumException
	 * @throws UnexpectedResponseException
	 * @throws TimeoutException
	 */
	public void sendChecksum(long CRC) throws IOException, TimeoutException, UnexpectedResponseException,
			InvalidChecksumException, NullPointerException {

		// log.debug("Send CRC after 20 Lines or end of Block");
		sendBootLoaderMessage(Messages.writeCRCRequestMessage(CRC));

		receiveBootLoaderReply(Messages.OK);
	}
	
	/**
	 * writes the byte array to the out stream pacemate style
	 * 
	 * @throws NullPointerException
	 * @throws InvalidChecksumException
	 * @throws UnexpectedResponseException
	 * @throws TimeoutException
	 */
	public void sendDataMessage(byte[] dataMessage) throws IOException, TimeoutException, UnexpectedResponseException,
			InvalidChecksumException, NullPointerException {
		// Allocate buffer for message + CR and LF
		int array_length = dataMessage.length + 2;

		byte[] data = new byte[array_length];

		// Copy message into the buffer
		System.arraycopy(dataMessage, 0, data, 0, dataMessage.length);

		// add CR and LF
		data[dataMessage.length] = 0x0D; // <CR>
		data[dataMessage.length + 1] = 0x0A; // <LF>

		// Print message
		// log.debug("Sending data msg: " + Tools.toASCIIString(data));

		// Send message
		OutputStream outStream = connection.getOutputStream();
		outStream.write(data);
		outStream.flush();

		receiveBootLoaderReply(Messages.DATA_ECHO);
	}

	public boolean waitForConnection() {
		try {
			// Send flash read request (in fact, this could be any valid message
			// to which the
			// device is supposed to respond)
			sendBootLoaderMessage(Messages.ReadPartIDRequestMessage());
			receiveBootLoaderReply(Messages.CMD_SUCCESS);
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
	
	/**
	 * Writes the CRC to the last two bytes of the Flash pacemate style
	 * 
	 * @param crc
	 * 
	 * @return everything OK
	 * @throws Exception
	 */
	public boolean writeCRCtoFlash(int crc) throws Exception {
		byte crc_bytes[] = new byte[256];
		for (int i = 0; i < 256; i++)
		crc_bytes[i] = (byte) 0xff;
		crc_bytes[254] = (byte) ((crc & 0xff00) >> 8);
		crc_bytes[255] = (byte) (crc & 0xff);

		log.debug("CRC = " + crc + " " + crc_bytes[254] + " " + crc_bytes[255]);

		try {
			configureFlash(14, 14);
		} catch (Exception e) {
			log.debug("Error while configure flash!");
			return false;
		}

		try {
			eraseFlash(14, 14);
		} catch (Exception e) {
			log.debug("Error while erasing flash!");
			return false;
		}

		try {
			writeToRAM(START_ADDRESS_IN_RAM, 256);
		} catch (Exception e) {
			log.debug("Error while write to RAM!");
			return false;
		}

		int counter = 0;

		int crc_checksum = 0;

		byte[] line = null;

		// each block is sent in parts of 20 lines a 45 bytes
		while (counter < crc_bytes.length) {
			int offset = 0;
			if (counter + 45 < crc_bytes.length) {
				line = new byte[PacemateBinData.LINESIZE]; // a line with 45 bytes
				System.arraycopy(crc_bytes, counter, line, 0, PacemateBinData.LINESIZE);
				counter = counter + PacemateBinData.LINESIZE;
			} else {
				if (((crc_bytes.length - counter) % 3) == 1)
					offset = 2;
				else if (((crc_bytes.length - counter) % 3) == 2)
					offset = 1;
				line = new byte[crc_bytes.length - counter + offset];
				line[line.length - 1] = 0;
				line[line.length - 2] = 0;
				System.arraycopy(crc_bytes, counter, line, 0, crc_bytes.length - counter);
				counter = counter + (crc_bytes.length - counter);
			}

			for (int i = 0; i < line.length; i++)
				crc_checksum = PacemateBinData.calcCRCChecksum(crc_checksum, line[i]);

			log.debug("Sending data msg: " + StringUtils.toHexString(line));

			sendDataMessage(PacemateBinData.encodeCRCData(line, (line.length - offset)));
		}

		try {
			sendChecksum(crc_checksum);
		} catch (Exception e) {
			log.debug("Error while sending checksum for crc!");
			return false;
		}

		// if block is completed copy data from RAM to Flash
		int crc_block_start = 0x3ff00;

		log.debug("Prepare Flash and Copy Ram to Flash 14 14 " + crc_block_start);

		try {
			configureFlash(14, 14);
			copyRAMToFlash(crc_block_start, START_ADDRESS_IN_RAM, 256);
		} catch (Exception e) {
			log.debug("Error while copy RAM to Flash!");
			return false;
		}

		return true;
	}
	
	/**
	 * Pacemate style
	 */
	public byte[] writeToRAM(long address, int len) throws Exception {
		// Send flash program request
		// log.debug("Sending program request for address " + address + " with " + data.length + " bytes");
		sendBootLoaderMessage(Messages.writeToRAMRequestMessage(address, len));

		// Read flash program response
		byte[] response = receiveBootLoaderReply(Messages.CMD_SUCCESS);

		// log.debug("write to RAM ok");

		return response;
	}
}
