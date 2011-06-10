package eu.smartsantander.wsn.drivers.waspmote;

import java.util.ArrayList;
import java.util.List;

import de.uniluebeck.itm.wsn.drivers.core.io.ByteReceiver;
import de.uniluebeck.itm.wsn.drivers.core.io.MessagePacketListener;


public class XBeeMessagePacketReceiver implements ByteReceiver {

	/**
	 * List for listeners that want to be notified on connection state changes.
	 */
	private final List<MessagePacketListener> listeners = new ArrayList<MessagePacketListener>();

	/**
	 * Data buffer for <code>MessagePacket</code> objects.
	 */
	private byte[] packet;

	/**
	 * Current packetLength of the received packet.
	 */
	private int packetLength = 0;

	/**
	 * Flag that will be set when a packet was received.
	 */
	private boolean readingPacket = false;

	@Override
	public void beforeReceive() {

	}

	@Override
	public void onReceive(final byte input) {
		// Check if packet is being read
		if(readingPacket) {
			packet[packetLength++] = input;
			if(packet.length == packetLength) {
				clearPacket();
				onMessagePacketReceived(packet);
			}
		} else {
			//if not, first read byte is packet length
			int packetLength = input;
			packet = new byte[packetLength];
			readingPacket = true;
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
		readingPacket = false;
	}

	/**
	 * Override this method for processing a received <code>MessagePacket</code>.
	 *
	 * @param packet The received message packet.
	 */
	public void onMessagePacketReceived(final byte[] packet) {
		for (final MessagePacketListener listener : listeners.toArray(new MessagePacketListener[listeners.size()])) {
			listener.onMessagePacketReceived(packet);
		}
	}

	public void addListener(final MessagePacketListener listener) {
		listeners.add(listener);
	}

	public void removeListener(final MessagePacketListener listener) {
		listeners.remove(listener);
	}
}