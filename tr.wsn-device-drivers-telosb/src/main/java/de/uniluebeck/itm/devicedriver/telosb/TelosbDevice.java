package de.uniluebeck.itm.devicedriver.telosb;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.AbstractDevice;
import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.ConnectionListener;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractGetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.AbstractReadMacAddressOperation;
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
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortLeaveProgramModeOperation;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortSendOperation;
import de.uniluebeck.itm.devicedriver.util.StringUtils;

public class TelosbDevice extends AbstractDevice implements ConnectionListener, SerialPortEventListener {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(TelosbDevice.class);
	
	private final SerialPortConnection connection;
	
	private BSLTelosb bsl;
	
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
	
	public TelosbDevice(SerialPortConnection connection) {
		this.connection = connection;
		this.connection.addConnectionListener(this);
	}
	
	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public int[] getChannels() {
		return null;
	}

	public EnterProgramModeOperation createEnterProgramModeOperation() {
		return new TelosbEnterProgramModeOperation(connection, bsl);
	}
	
	public LeaveProgramModeOperation createLeaveProgramModeOperation() {
		return new SerialPortLeaveProgramModeOperation(connection);
	}
	
	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		return new AbstractGetChipTypeOperation() {
			@Override
			public ChipType execute(Monitor monitor) throws Exception {
				return ChipType.TelosB;
			}
		};
	}

	@Override
	public ProgramOperation createProgramOperation() {
		return new TelosbProgramOperation(this);
	}

	@Override
	public EraseFlashOperation createEraseFlashOperation() {
		return new TelosbEraseFlashOperation(bsl);
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		return new TelosbWriteFlashOperation(bsl);
	}

	public ReadFlashOperation createReadFlashOperation() {
		return new TelosbReadFlashOperation(bsl);
	}

	public ReadMacAddressOperation createReadMacAddressOperation() {
		return new AbstractReadMacAddressOperation() {
			@Override
			public MacAddress execute(Monitor monitor) throws Exception {
				log.warn("readMacAddress is not implemented.");
				return null;
			}
		};
	}

	public WriteMacAddressOperation createWriteMacAddressOperation() {
		return new AbstractWriteMacAddressOperation() {
			@Override
			public Void execute(Monitor monitor) throws Exception {
				log.warn("writeMacAddress is not implemented.");
				return null;
			}
		};
	}

	public ResetOperation createResetOperation() {
		return new TelosbResetOperation(bsl);
	}

	public SendOperation createSendOperation() {
		return new SerialPortSendOperation(connection);
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:
			synchronized (bsl.dataAvailableMonitor) {
				bsl.dataAvailableMonitor.notifyAll();
			}
			receivePacket(connection.getInputStream());
			break;
		default:
			log.debug("Serial event (other than data available): " + event);
			break;
		}
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
				log.warn(e.getMessage());
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

	@Override
	public void onConnectionChange(Connection connection, boolean connected) {
		if (connected) {
			try {
				this.connection.getSerialPort().addEventListener(this);
			} catch (TooManyListenersException e) {
				log.error("Can not register serial port listener", e);
			}
			bsl = new BSLTelosb(this.connection);
		}
	}
}
