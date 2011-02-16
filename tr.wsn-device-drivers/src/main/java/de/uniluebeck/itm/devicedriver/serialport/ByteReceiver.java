package de.uniluebeck.itm.devicedriver.serialport;

import de.uniluebeck.itm.devicedriver.Device;


/**
 * Interface for processing incoming bytes from an serial port connection.
 * 
 * @author Malte Legenhausen
 */
public interface ByteReceiver {

	/**
	 * Set the device instance that will receive the data from the physical device.
	 * 
	 * @param device The device instance.
	 */
	void setDevice(Device<?> device);
	
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
