package de.uniluebeck.itm.devicedriver.jennec;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.AbstractDevice;
import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.ConnectionListener;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.Sector;
import de.uniluebeck.itm.devicedriver.SerialPortConnection;
import de.uniluebeck.itm.devicedriver.exception.FlashConfigurationFailedException;
import de.uniluebeck.itm.devicedriver.exception.FlashEraseFailedException;
import de.uniluebeck.itm.devicedriver.exception.FlashTypeReadFailedException;
import de.uniluebeck.itm.devicedriver.exception.InvalidChecksumException;
import de.uniluebeck.itm.devicedriver.exception.UnexpectedResponseException;
import de.uniluebeck.itm.devicedriver.jennec.exception.SectorEraseException;
import de.uniluebeck.itm.devicedriver.operation.EraseFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.ProgramOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.devicedriver.operation.ResetOperation;
import de.uniluebeck.itm.devicedriver.operation.SendOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteMacAddressOperation;
import de.uniluebeck.itm.devicedriver.util.StringUtils;
import de.uniluebeck.itm.devicedriver.util.TimeDiff;

public class JennecDevice extends AbstractDevice implements ConnectionListener, SerialPortEventListener {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(JennecDevice.class);
	
	private static final int TIMEOUT = 2000;
	
	
	private final Object dataAvailableMonitor = new Object();
	
	/**
	 * Data buffer for incoming data 
	 */
	private byte[] packet = new byte[2048];

	/** 
	 * Current packetLength of the received packet 
	 */
	private int packetLength = 0;

	/** */
	private boolean foundDLE = false;

	/** */
	private boolean foundPacket = false;
	
	private final SerialPortConnection connection;
	
	public JennecDevice(SerialPortConnection connection) {
		this.connection = connection;
		this.connection.addConnectionListener(this);
	}
	
	public EnterProgramModeOperation createEnterProgramModeOperation() {
		return new JennecEnterProgramModeOperation(connection);
	}
	
	@Override
	public EraseFlashOperation createEraseFlashOperation() {
		return new JennecEraseFlashOperation(this);
	}

	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		return new JennecGetChipTypeOperation(this);
	}
	
	public GetFlashHeaderOperation createGetFlashHeaderOperation() {
		return new JennecGetFlashHeaderOperation(this);
	}

	@Override
	public ProgramOperation createProgramOperation() {
		return new JennecProgramOperation(this);
	}

	@Override
	public ReadFlashOperation createReadFlashOperation() {
		return new JennecReadFlashOperation(this);
	}

	@Override
	public ReadMacAddressOperation createReadMacAddressOperation() {
		return new JennecReadMacAddressOperation(this);
	}

	@Override
	public ResetOperation createResetOperation() {
		return new JennecResetOperation(connection);
	}

	@Override
	public SendOperation createSendOperation() {
		return new JennecSendOperation(connection);
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		return new JennecWriteFlashOperation(this);
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		return new JennecWriteMacAddressOperation(this);
	}

	@Override
	public int[] getChannels() {
		return new int[] { 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:
			synchronized (dataAvailableMonitor) {
				dataAvailableMonitor.notifyAll();
			}
			receivePacket(connection.getInputStream());
			break;
		default:
			log.debug("Serial event (other than data available): " + event);
			break;
		}
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
	
	private void receivePacket(InputStream inStream) {
		try {
			while (inStream != null && inStream.available() > 0) {
				byte c = (byte) (0xff & inStream.read());

				// Check if DLE was found
				if (foundDLE) {
					foundDLE = false;

					if (c == MessagePacket.STX && !foundPacket) {
						//log.debug("iSenseDeviceImpl: STX received in DLE mode");
						foundPacket = true;
					} else if (c == MessagePacket.ETX && foundPacket) {
						//log.debug("ETX received in DLE mode");

						// Parse message and notify listeners
						MessagePacket p = MessagePacket.parse(packet, 0, packetLength);
						// p.setIsenseDevice(this);
						//log.debug("Packet found: " + p);
						notifyMessagePacketListener(p);

						// Reset packet information
						clearPacket();
					} else if (c == MessagePacket.DLE && foundPacket) {
						// Stuffed DLE found
						//log.debug("Stuffed DLE received in DLE mode");
						ensureBufferSize();
						packet[packetLength++] = MessagePacket.DLE;
					} else {
						log.error("Incomplete packet received: " + StringUtils.toHexString(this.packet, 0, packetLength));
						clearPacket();
					}

				} else {
					if (c == MessagePacket.DLE) {
						log.debug("Plain DLE received");
						foundDLE = true;
					} else if (foundPacket) {
						ensureBufferSize();
						packet[packetLength++] = c;
					}
				}
			}

		} catch (IOException error) {
			log.error("Error on rx (Retry in 1s): " + error, error);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}
	
	/**
	 * 
	 */
	private void ensureBufferSize() {
		if (packetLength + 1 >= this.packet.length) {
			byte tmp[] = new byte[packetLength + 100];
			System.arraycopy(this.packet, 0, tmp, 0, packetLength);
			this.packet = tmp;
		}
	}
	
	private void clearPacket() {
		packetLength = 0;
		foundDLE = false;
		foundPacket = false;
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
		if (checksum != recvChecksum)
			throw new InvalidChecksumException();

		// Check if the response type is unexpected
		if (message[0] != type)
			throw new UnexpectedResponseException(type, message[0]);

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
	
	/**
	 * Wait at most timeoutMillis for the input stream to become available
	 * 
	 * @param timeoutMillis Milliseconds to wait until timeout, 0 for no timeout
	 * @return The number of characters available
	 * @throws IOException
	 */
	public int waitDataAvailable(int timeoutMillis) throws TimeoutException, IOException {
		InputStream inputStream = connection.getInputStream();
		TimeDiff timeDiff = new TimeDiff();
		int available = 0;

		while (inputStream != null && (available = inputStream.available()) == 0) {
			if (timeoutMillis > 0 && timeDiff.ms() >= timeoutMillis) {
				log.warn("Timeout waiting for data (waited: " + timeDiff.ms() + ", timeoutMs:" + timeoutMillis + ")");
				throw new TimeoutException();
			}

			synchronized (dataAvailableMonitor) {
				try {
					dataAvailableMonitor.wait(50);
				} catch (InterruptedException e) {
					log.error("Interrupted: " + e, e);
				}
			}
		}
		return available;
	}

	@Override
	public void onConnectionChange(Connection connection, boolean connected) {
		if (connected) {
			try {
				this.connection.getSerialPort().addEventListener(this);
			} catch (TooManyListenersException e) {
				log.error("Can not register serial port listener", e);
			}
		}
	}
}
