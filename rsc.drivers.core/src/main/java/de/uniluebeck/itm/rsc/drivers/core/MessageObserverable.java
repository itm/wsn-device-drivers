package de.uniluebeck.itm.rsc.drivers.core;


/**
 * Interface that defines method for Message observation of an object.
 * 
 * @author Malte Legenhausen
 */
public interface MessageObserverable {

	/**
	 * Stores an handler that will be called when a given type occure.
	 * 
	 * @param listener The listener that will be called.
	 * @param types The types that specify when the handler is called.
	 */
	void addListener(MessagePacketListener listener, PacketType... types);
	
	/**
	 * Add an handler that will be called when the given byte types occure.
	 * 
	 * @param listener The listener that will be called.
	 * @param types The types as byte array that specify when the listener is called.
	 */
	void addListener(MessagePacketListener listener, int... types);
	
	/**
	 * Add an handler that will be called on all message packet types.
	 * 
	 * @param listener The listener that will be called.
	 */
	void addListener(MessagePacketListener listener);
	
	/**
	 * Add an handler that will be called when a <code>MessagePlainText</code> can be send.
	 * 
	 * @param listener The listener that will be called.
	 */
	void addListener(MessagePlainTextListener listener);
	
	/**
	 * Remove the given handler from the handler list.
	 * 
	 * @param listener The listener that has to be removed.
	 */
	void removeListener(MessagePacketListener listener);
	
	/**
	 * Remove the the given listener from the handler list.
	 * 
	 * @param listener The listener that has to be removed.
	 */
	void removeListener(MessagePlainTextListener listener);
}
