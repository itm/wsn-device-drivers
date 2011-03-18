package de.uniluebeck.itm.rsc.drivers.core;

import de.uniluebeck.itm.rsc.drivers.core.event.MessageEvent;

/**
 * Listener that is called when an <code>MessagePacket</code> receivced.
 * 
 * @author Malte Legenhausen
 */
public interface MessagePlainTextListener {

	/**
	 * Debug output as <code>MessagePleinText</code> object.
	 * 
	 * @param message The received message as <code>MessagePlainText</code>.
	 */
	void onMessagePlainTextReceived(MessageEvent<MessagePlainText> message);
}
