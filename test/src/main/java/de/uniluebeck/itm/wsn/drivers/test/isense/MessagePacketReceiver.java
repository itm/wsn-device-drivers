package de.uniluebeck.itm.wsn.drivers.test.isense;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.test.ByteReceiver;

public class MessagePacketReceiver implements ByteReceiver {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MessagePacketReceiver.class);

	/**
	 * The maximum length of a message packet.
	 */
	private static final int LENGTH = 2048;

	/**
	 * Length of the buffer that will be added when the message is longer than LENGTH.
	 */
	private static final int ADDIONAL_LENGTH = 100;

	/**
	 * Data buffer for <code>de.uniluebeck.itm.wsn.drivers.test.MessagePacket</code> objects.
	 */
	private byte[] packet = new byte[LENGTH];

	/** 
	 * Current packetLength of the received packet.
	 */
	private int packetLength = 0;

	/**
	 * DLE packet found flag.
	 */
	private boolean foundDLE = false;

	/**
	 * Flag that will be set when a packet was received. 
	 */
	private boolean foundPacket = false;

	@Override
	public void beforeReceive() {

	}

	@Override
	public void onReceive(final byte input) {
		// Check if DLE was found
		if (foundDLE) {
			foundDLE = false;

			if (input == MessagePacket.STX && !foundPacket) {
				//log.debug("iSenseDeviceImpl: STX received in DLE mode");
				foundPacket = true;
			} else if (input == MessagePacket.ETX && foundPacket) {
				//log.debug("ETX received in DLE mode");

				// Parse message and notify listeners
				final MessagePacket message = MessagePacket.parse(packet, 0, packetLength);
				// p.setIsenseDevice(this);
				//log.debug("Packet found: " + p);
				onMessagePacketReceived(message);

				// Reset packet information
				clearPacket();
			} else if (input == MessagePacket.DLE && foundPacket) {
				// Stuffed DLE found
				//log.debug("Stuffed DLE received in DLE mode");
				ensureBufferSize();
				packet[packetLength++] = MessagePacket.DLE;
			} else {
				LOG.error("Incomplete packet received: " + StringUtils.toHexString(this.packet, 0, packetLength));
				clearPacket();
			}
		} else {
			if (input == MessagePacket.DLE) {
				LOG.debug("Plain DLE received");
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
			final byte tmp[] = new byte[packetLength + ADDIONAL_LENGTH];
			System.arraycopy(this.packet, 0, tmp, 0, packetLength);
			this.packet = tmp;
		}
	}
	
	/**
	 * Override this method for processing a received <code>de.uniluebeck.itm.wsn.drivers.test.MessagePacket</code>.
	 * 
	 * @param packet The received message packet.
	 */
	public void onMessagePacketReceived(final MessagePacket packet) {
		System.out.println(new String(packet.getContent()).substring(1));
	}
}
