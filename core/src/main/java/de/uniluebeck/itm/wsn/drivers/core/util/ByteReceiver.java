package de.uniluebeck.itm.wsn.drivers.core.util;

/**
 * Interface for processing incoming bytes from an serial port connection.
 * 
 * @author Malte Legenhausen
 */
public interface ByteReceiver {

	/**
	 * This method is called before data is received.
	 */
	void beforeReceive();

	/**
	 * This method is called when data was received.
	 * 
	 * @param data The received data.
	 */
	void onReceive(byte data);

	/**
	 * This method is called after all data was received.
	 */
	void afterReceive();
}
