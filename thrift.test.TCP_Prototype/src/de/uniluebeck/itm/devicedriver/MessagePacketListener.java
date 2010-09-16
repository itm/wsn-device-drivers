package de.uniluebeck.itm.devicedriver;

/**
 * Listener that is called when an <code>MessagePacket</code> receivced.
 * 
 * @author Malte Legenhausen
 */
public interface MessagePacketListener {

	/**
	 * Method is called when a <code>MessagePacket</code> was received.
	 * 
	 * @param message The received <code>MessagePacket</code>.
	 */
	void onMessagePacketReceived(MessagePacket message);

	/**
	 * Debug output as <code>MessagePleinText</code> object.
	 * 
	 * @param message The received message as <code>MessagePlainText</code>.
	 */
	void onMessagePlainTextReceived(MessagePlainText message);
}
