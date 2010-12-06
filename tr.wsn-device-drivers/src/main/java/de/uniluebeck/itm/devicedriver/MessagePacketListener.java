package de.uniluebeck.itm.devicedriver;

import de.uniluebeck.itm.devicedriver.event.MessageEvent;

/**
 * Listener that is called when an <code>MessagePacket</code> receivced.
 * 
 * @author Malte Legenhausen
 */
public interface MessagePacketListener {

	/**
	 * Method is called when a <code>MessagePacket</code> was received.
	 * 
	 * @param event The received <code>MessagePacket</code>.
	 */
	void onMessagePacketReceived(MessageEvent<MessagePacket> event);
}
