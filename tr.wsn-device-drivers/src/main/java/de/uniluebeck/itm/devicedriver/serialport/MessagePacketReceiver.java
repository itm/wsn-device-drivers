package de.uniluebeck.itm.devicedriver.serialport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;
import de.uniluebeck.itm.tr.util.StringUtils;

public class MessagePacketReceiver implements ByteReceiver {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(MessagePacketReceiver.class);
	
	/**
	 * Data buffer for <code>MessagePacket</code> objects.
	 */
	private byte[] packet = new byte[2048];

	/** 
	 * Current packetLength of the received packet.
	 */
	private int packetLength = 0;

	/**
	 * 
	 */
	private boolean foundDLE = false;

	/**
	 * Flag that will be set when a packet was received. 
	 */
	private boolean foundPacket = false;
	
	/**
	 * The device from where all methods will be called.
	 */
	private AbstractSerialPortDevice device;
	
	@Override
	public void setDevice(Device device) {
		this.device = (AbstractSerialPortDevice) device;
	}

	@Override
	public void beforeReceive() {
		
	}

	@Override
	public void onReceive(byte input) {
		// Check if DLE was found
		if (foundDLE) {
			foundDLE = false;

			if (input == MessagePacket.STX && !foundPacket) {
				//log.debug("iSenseDeviceImpl: STX received in DLE mode");
				foundPacket = true;
			} else if (input == MessagePacket.ETX && foundPacket) {
				//log.debug("ETX received in DLE mode");

				// Parse message and notify listeners
				final MessagePacket p = MessagePacket.parse(packet, 0, packetLength);
				// p.setIsenseDevice(this);
				//log.debug("Packet found: " + p);
				device.fireMessagePacketEvent(new MessageEvent<MessagePacket>(this, p));

				// Reset packet information
				clearPacket();
			} else if (input == MessagePacket.DLE && foundPacket) {
				// Stuffed DLE found
				//log.debug("Stuffed DLE received in DLE mode");
				ensureBufferSize();
				packet[packetLength++] = MessagePacket.DLE;
			} else {
				log.error("Incomplete packet received: " + StringUtils.toHexString(this.packet, 0, packetLength));
				clearPacket();
			}
		} else {
			if (input == MessagePacket.DLE) {
				log.debug("Plain DLE received");
				foundDLE = true;
			} else if (foundPacket) {
				ensureBufferSize();
				packet[packetLength++] = input;
			}
		}
	}

	@Override
	public void afterReceive() {
		
	}
	
	/**
	 * Reset the internal state.
	 */
	private void clearPacket() {
		packetLength = 0;
		foundDLE = false;
		foundPacket = false;
	}

	/**
	 * Increase the buffer size if necessary.
	 */
	private void ensureBufferSize() {
		if (packetLength + 1 >= this.packet.length) {
			byte tmp[] = new byte[packetLength + 100];
			System.arraycopy(this.packet, 0, tmp, 0, packetLength);
			this.packet = tmp;
		}
	}
}
