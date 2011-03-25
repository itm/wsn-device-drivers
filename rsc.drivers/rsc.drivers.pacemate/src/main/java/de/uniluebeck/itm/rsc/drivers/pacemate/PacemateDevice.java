package de.uniluebeck.itm.rsc.drivers.pacemate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.rsc.drivers.core.Monitor;
import de.uniluebeck.itm.rsc.drivers.core.Programable;
import de.uniluebeck.itm.rsc.drivers.core.exception.InvalidChecksumException;
import de.uniluebeck.itm.rsc.drivers.core.exception.TimeoutException;
import de.uniluebeck.itm.rsc.drivers.core.exception.UnexpectedResponseException;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractWriteFlashOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractWriteMacAddressOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.SendOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.WriteFlashOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.WriteMacAddressOperation;
import de.uniluebeck.itm.rsc.drivers.core.serialport.AbstractSerialPortDevice;
import de.uniluebeck.itm.rsc.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.rsc.drivers.core.serialport.SerialPortEnterProgramModeOperation;
import de.uniluebeck.itm.rsc.drivers.core.serialport.SerialPortLeaveProgramModeOperation;
import de.uniluebeck.itm.rsc.drivers.core.serialport.SerialPortSendOperation;
import de.uniluebeck.itm.rsc.drivers.isense.iSenseResetOperation;
import de.uniluebeck.itm.tr.util.StringUtils;

public class PacemateDevice extends AbstractSerialPortDevice implements Programable {

	/**
	 * This is the Start Address in the RAM to write data
	 */
	public static final long START_ADDRESS_IN_RAM = 1073742336;
	
	private static final Logger log = LoggerFactory.getLogger(PacemateDevice.class);
	
	private static final int TIMEOUT = 2000;
	
	private boolean echo = true;

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
		final EnterProgramModeOperation operation = new SerialPortEnterProgramModeOperation(getConnection());
		monitorState(operation);
		return operation;
	}
	
	@Override
	public LeaveProgramModeOperation createLeaveProgramModeOperation() {
		final LeaveProgramModeOperation operation = new SerialPortLeaveProgramModeOperation(getConnection());
		monitorState(operation);
		return operation;
	}

	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		final GetChipTypeOperation operation = new PacemateGetChipTypeOperation(this);
		monitorState(operation);
		return operation;
	}

	@Override
	public ProgramOperation createProgramOperation() {
		final ProgramOperation operation = new PacemateProgramOperation(this);
		monitorState(operation);
		return operation;
	}

	@Override
	public EraseFlashOperation createEraseFlashOperation() {
		final EraseFlashOperation operation = new PacemateEraseFlashOperation(this);
		monitorState(operation);
		return operation;
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		final WriteFlashOperation operation = new AbstractWriteFlashOperation() {
			@Override
			public Void execute(Monitor monitor) throws Exception {
				throw new UnsupportedOperationException("writeFlash is not available.");
			}
		};
		return operation;
	}

	@Override
	public ReadFlashOperation createReadFlashOperation() {
		final ReadFlashOperation operation = new PacemateReadFlashOperation(this);
		monitorState(operation);
		return operation;
	}

	@Override
	public ReadMacAddressOperation createReadMacAddressOperation() {
		final ReadMacAddressOperation operation = new PacemateReadMacAddressOperation(this);
		monitorState(operation);
		return operation;
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		final WriteMacAddressOperation operation = new AbstractWriteMacAddressOperation() {
			@Override
			public Void execute(Monitor monitor) throws Exception {
				throw new UnsupportedOperationException("writeMacAddress ist not available.");
			}
		};
		return operation;
	}

	@Override
	public ResetOperation createResetOperation() {
		final ResetOperation operation = new iSenseResetOperation(getConnection());
		monitorState(operation);
		return operation;
	}

	@Override
	public SendOperation createSendOperation() {
		final SendOperation operation = new SerialPortSendOperation(getConnection());
		monitorState(operation);
		return operation;
	}
	
	public boolean isEcho() {
		return echo;
	}

	public void setEcho(boolean echo) {
		this.echo = echo;
	}

	/**
	 * pacemate style
	 */
	public void sendBootLoaderMessage(byte[] message) throws IOException {
		// Allocate buffer for message + CR and LF
		byte[] data = new byte[message.length + 2];

		// Copy message into the buffer
		System.arraycopy(message, 0, data, 0, message.length);

		// add CR and LF
		data[data.length - 2] = 0x0D; // <CR>
		data[data.length - 1] = 0x0A; // <LF>

		// Send message
		final OutputStream outputStream = getConnection().getOutputStream();
		outputStream.write(data);
		outputStream.flush();
	}
	
	public void clearStreamData() throws IOException {

		final InputStream inStream = getConnection().getInputStream();
		
		// Allocate message buffer max 255 bytes to read
		byte[] message = new byte[255];

		int index = 0;

		// Read the data
		boolean a = true;
		while ((inStream.available() > 0) && (a == true) && (index < 255)) {
			try {
				//System.out.println("************ Reading from stream");
				message[index] = (byte) inStream.read();
				//System.out.println("************ Done reading from stream");
			} catch (IOException e) {
				log.error("" + e, e);
			}
			if (message[index] == -1) {
				a = false;
			} else {
				index++;
			}
		}
	}
	
	public void configureFlash(int start, int end) throws Exception {
		log.debug("Configuring flash from " + start + " to " + end + "...");

		enableFlashErase();

		// Send flash configure request
		sendBootLoaderMessage(Messages.flashConfigureRequestMessage(start, end));

		// Read flash configure response
		receiveBootLoaderReplySuccess(Messages.CMD_SUCCESS);

		log.debug("Flash is configured");
	}
	
	/**
	 * Pacemate style
	 */
	public void copyRAMToFlash(long flashAddress, long ramAddress, int length) throws Exception {
		// Send flash program request
		log.debug("Sending program request for address " + ramAddress + " with " + length + " bytes");
		sendBootLoaderMessage(Messages.copyRAMToFlashRequestMessage(flashAddress, ramAddress, length));

		// Read flash program response
		receiveBootLoaderReplySuccess(Messages.CMD_SUCCESS);

		log.debug("Copy Ram to Flash ok");
	}
	
	public void eraseFlash(int start, int end) throws Exception {
		log.debug("Erasing sector from " + start + " to " + end + "...");
		sendBootLoaderMessage(Messages.flashEraseRequestMessage(start, end));
		receiveBootLoaderReplySuccess(Messages.CMD_SUCCESS);
		try {
			receiveBootLoaderReplySuccess(Messages.CMD_SUCCESS);
		} catch (TimeoutException e) {
			log.debug("one line erase response");
		}
		log.debug("Flash erased");
	}
	
	public void enableFlashErase() throws Exception {
		log.debug("Enabling Erase Flash...");
		sendBootLoaderMessage(Messages.Unlock_RequestMessage());
		receiveBootLoaderReplySuccess(Messages.CMD_SUCCESS);
	}

	/**
	 * Receive the bsl reply message to all request messages with a success answer
	 *
	 * @param type
	 *
	 * @return
	 *
	 * @throws TimeoutException
	 * @throws UnexpectedResponseException
	 * @throws InvalidChecksumException
	 * @throws IOException
	 * @throws NullPointerException
	 */
	protected String receiveBootLoaderReplySuccess(String type)
			throws TimeoutException, UnexpectedResponseException, InvalidChecksumException, IOException,
			NullPointerException {

		byte[] reply;

		if (this.echo == true) {
			reply = readInputStream(3);
		} else {
			reply = readInputStream(2);
		}

		String replyStr = StringUtils.toASCIIString(reply);

		// split the lines from the response message
		String[] parts = replyStr.split("<CR><LF>");

		for (final String part : parts) {
			log.debug("BL parts " + part);
		}

		// does the node echo all messages or not
		if (echo == true) {
			if (parts.length >= 2) {
				if (parts[1].compareTo("0") == 0) // 0 = everything is OK
				{
					if (parts.length >= 3) {
						return parts[2];
					} else {
						return "";
					}
				}
			}
		} else {
			if (parts.length >= 1) {
				if (parts[0].compareTo("0") == 0) // 0 = everything is OK
				{
					if (parts.length >= 1) {
						return parts[1];
					} else {
						return "";
					}
				}
			}
		}

		throw new UnexpectedResponseException("Error in response *" + replyStr + "*", -1, -1);
	}


	/**
	 * Receive the BSL reply message for the autobaud / synchronize request
	 *
	 * @param type
	 *
	 * @return
	 *
	 * @throws TimeoutException
	 * @throws UnexpectedResponseException
	 * @throws InvalidChecksumException
	 * @throws IOException
	 * @throws NullPointerException
	 */
	protected byte[] receiveBootLoaderReplySynchronized(String type)
			throws TimeoutException, UnexpectedResponseException, InvalidChecksumException, IOException,
			NullPointerException {

		byte[] reply = null;
		if (type.compareTo(Messages.SYNCHRONIZED) == 0) {
			reply = readInputStream(1);
		} else {
			reply = readInputStream(3);
		}
		String replyStr = StringUtils.toASCIIString(reply);

		if ((replyStr.compareTo("Synchronized<CR><LF>") == 0)
				|| (replyStr.compareTo("Synchronized<CR><LF>OK<CR><LF>") == 0)) {
			return reply;
		} else if ((type.compareTo(Messages.SYNCHRONIZED_OK) == 0)) {
			return reply;
		}

		throw new UnexpectedResponseException("Wrong response " + StringUtils.toASCIIString(reply) + " and not " + type,
				-1, -1
		);
	}

	/**
	 * Read the echo for a line of data
	 *
	 * @return
	 * @throws TimeoutException
	 * @throws UnexpectedResponseException
	 * @throws InvalidChecksumException
	 * @throws IOException
	 * @throws NullPointerException
	 */
	protected String receiveBootLoaderReplySendDataEcho()
			throws TimeoutException, UnexpectedResponseException, InvalidChecksumException, IOException,
			NullPointerException {

		byte[] reply;

		reply = readInputStream(1);

		String replyStr = StringUtils.toASCIIString(reply);

		return replyStr;
	}

	/**
	 * Read the requested line from the Flash
	 *
	 * @return
	 * @throws TimeoutException
	 * @throws UnexpectedResponseException
	 * @throws InvalidChecksumException
	 * @throws IOException
	 * @throws NullPointerException
	 */
	protected byte[] receiveBootLoaderReplyReadData()
			throws TimeoutException, UnexpectedResponseException, InvalidChecksumException, IOException,
			NullPointerException {

		byte[] reply = null;
		if (this.echo) {
			reply = readInputStream(4);
		} else {
			reply = readInputStream(3);
		}

		int i = 0;
		if (this.echo) {
			for (i = 0; i < reply.length; i++) {
				if (reply[i] == 13) { // skip the echo and cr
					i = i + 2; // and lf as well
					break;
				}
			}
		}
		int len = (reply.length - (i + 5));

		byte[] lineFromFlash = new byte[len];

		if ((i + 3 < reply.length) && (reply[i] == 48)) { // copy the line and skip the answer and cr lf
			System.arraycopy(reply, i + 3, lineFromFlash, 0, len);
			//System.out.println(StringUtils.toASCIIString(lineFromFlash));
			return lineFromFlash;
		}

		throw new UnexpectedResponseException("Error in response *" + StringUtils.toASCIIString(reply) + "*", -1, -1);
	}

	/**
	 * Read the response to the CRC message
	 *
	 * @return
	 *
	 * @throws TimeoutException
	 * @throws UnexpectedResponseException
	 * @throws InvalidChecksumException
	 * @throws IOException
	 * @throws NullPointerException
	 */
	protected byte[] receiveBootLoaderReplyReadCRCOK()
			throws TimeoutException, UnexpectedResponseException, InvalidChecksumException, IOException,
			NullPointerException {

		byte[] reply = null;
		if (this.echo) {
			reply = readInputStream(2);
		} else {
			reply = readInputStream(1);
		}

		String replyStr = StringUtils.toASCIIString(reply);

		// split the lines from the response message
		String[] parts = replyStr.split("<CR><LF>");

		if (this.echo) {
			if (parts[1].compareTo(Messages.OK) == 0) {
				return reply;
			} else {
				log.debug("Received boot loader msg: " + replyStr);
				throw new InvalidChecksumException("Invalid checksum - resend " + replyStr);
			}
		} else {
			if (parts[0].compareTo(Messages.OK) == 0) {
				return reply;
			} else {
				log.debug("Received boot loader msg: " + replyStr);
				throw new InvalidChecksumException("Invalid checksum - resend " + replyStr);
			}
		}
	}

	/**
	 * Read from the Input stream from the Pacemate. The length of the expected pacemate reply message is given with the
	 * expected number of  cr lf chars
	 *
	 * @param CRLFcount
	 *
	 * @return
	 *
	 * @throws TimeoutException
	 * @throws IOException
	 */
	private byte[] readInputStream(int CRLFcount) throws TimeoutException, IOException {
		final byte[] message = new byte[255];

		int index = 0;
		int counter = 0;
		int wait = 5;
		waitDataAvailable(TIMEOUT);

		// Read the message - read CRLFcount lines of response
		final InputStream inStream = getConnection().getInputStream();
		while ((index < 255) && (counter < CRLFcount)) {
			if (inStream.available() > 0) {
				message[index] = (byte) inStream.read();
				if (message[index] == 0x0a) {
					counter++;
				}
				if (message[index] != -1) {
					index++;
				}
			} else {
				// message is smaller then expected
				// check if the last line was cr lf 0 cr lf == Success message without more infos
				if (index >= 5 && checkResponseMessage(message, index)) {
					break;
				}
				
				try {
					waitDataAvailable(1000);
				} catch (final TimeoutException e) {
					// Do nothing
				}
				
				wait--;
				if (wait == 0) {
					final byte[] fullMessage = new byte[index];
					System.arraycopy(message, 0, fullMessage, 0, index);
					throw new TimeoutException("Not a complete response message from the node *" + StringUtils
							.toASCIIString(fullMessage) + "*"
					);
				}
			}
		}

		// copy to real length
		byte[] fullMessage = new byte[index];
		System.arraycopy(message, 0, fullMessage, 0, index);
		log.debug("read lines " + StringUtils.toASCIIString(fullMessage));
		return fullMessage;
	}

	/**
	 * Check if the last received bytes were cr lf 0 cr lf == Success message without more infos
	 *
	 * @param message
	 * @param index
	 */
	private boolean checkResponseMessage(byte[] message, int index) {
		//log.info("Check Response "+message[index-5]+" "+message[index-4]+" "+message[index-3]+" "+message[index-2]+" "+message[index-1]);
		if ((message[index - 5] == 13)		 // cr
				&& (message[index - 4] == 10)  // lf
				&& (message[index - 3] == 48)  // 0
				&& (message[index - 2] == 13)  // cr
				&& (message[index - 1] == 10)) // lf
		{
			return true;
		}
		return false;
	}

	protected boolean waitForConnection() {
		try {
			// Send flash read request (in fact, this could be any valid message
			// to which the
			// device is supposed to respond)
			sendBootLoaderMessage(Messages.ReadPartIDRequestMessage());
			receiveBootLoaderReplySuccess(Messages.CMD_SUCCESS);
			log.debug("Device connection established");
			return true;
		} catch (TimeoutException to) {
			log.debug("Still waiting for a connection.");
		} catch (Exception error) {
			log.warn("Exception while waiting for connection", error);
		}

		getConnection().flush();
		return false;
	}

	public boolean autobaud() {
		try {
			sendBootLoaderMessage(Messages.AutoBaudRequestMessage());
			receiveBootLoaderReplySynchronized(Messages.SYNCHRONIZED);
			sendBootLoaderMessage(Messages.AutoBaudRequest2Message());
			receiveBootLoaderReplySynchronized(Messages.SYNCHRONIZED);
			sendBootLoaderMessage(Messages.AutoBaudRequest3Message());
			receiveBootLoaderReplySynchronized(Messages.SYNCHRONIZED_OK);
			log.debug("Autobaud");
		} catch (TimeoutException to) {
			log.debug("Still waiting for a connection.");
		} catch (Exception error) {
			log.warn("Exception while waiting for connection", error);
		}
		return true;
	}
	
	/**
	 * Pacemate style
	 */
	public void writeToRAM(long address, int len) throws Exception {
		// Send flash program request
		// log.debug("Sending program request for address " + address + " with " + data.length + " bytes");
		sendBootLoaderMessage(Messages.writeToRAMRequestMessage(address, len));
		//System.out.println("send ready");
		// Read flash program response
		receiveBootLoaderReplySuccess(Messages.CMD_SUCCESS);

		// log.debug("write to RAM ok");
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
		final OutputStream outputStream = getConnection().getOutputStream();
		outputStream.write(data);
		outputStream.flush();

		receiveBootLoaderReplySendDataEcho();
	}
	
	/**
	 * writes the byte array to the out stream pacemate style
	 *
	 * @throws NullPointerException
	 * @throws InvalidChecksumException
	 * @throws UnexpectedResponseException
	 * @throws TimeoutException
	 */
	public void sendChecksum(long CRC) throws IOException, TimeoutException, UnexpectedResponseException, InvalidChecksumException, NullPointerException {

		// log.debug("Send CRC after 20 Lines or end of Block");
		sendBootLoaderMessage(Messages.writeCRCRequestMessage(CRC));

		receiveBootLoaderReplyReadCRCOK();
	}
	
	/**
	 * Writes the CRC to the last two bytes of the Flash pacemate style
	 *
	 * @param crc
	 *
	 * @return everything OK
	 *
	 * @throws Exception
	 */
	public boolean writeCRCtoFlash(int crc) throws Exception {
		byte crc_bytes[] = new byte[256];
		for (int i = 0; i < 256; i++) {
			crc_bytes[i] = (byte) 0xff;
		}
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
				if (((crc_bytes.length - counter) % 3) == 1) {
					offset = 2;
				} else if (((crc_bytes.length - counter) % 3) == 2) {
					offset = 1;
				}
				line = new byte[crc_bytes.length - counter + offset];
				line[line.length - 1] = 0;
				line[line.length - 2] = 0;
				System.arraycopy(crc_bytes, counter, line, 0, crc_bytes.length - counter);
				counter = counter + (crc_bytes.length - counter);
			}

			for (int i = 0; i < line.length; i++) {
				crc_checksum = PacemateBinData.calcCRCChecksum(crc_checksum, line[i]);
			}

			if (log.isDebugEnabled()) {
				log.debug("Sending data msg: " + StringUtils.toHexString(line));
			}

			sendDataMessage(PacemateBinData.encodeCRCData(line, line.length - offset));
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
}
