package de.uniluebeck.itm.wsn.drivers.core.io;

/**
 * Listener that is called when an <code>MessagePacket</code> received.
 *
 * @author TLMAT UC
 */
public interface MessagePacketListener {

	/**
	 * Method is called when a <code>MessagePacket</code> was received.
	 *
	 * @param event The received <code>MessagePacket</code>.
	 */
	void onMessagePacketReceived(byte[] event);
}
