package de.uniluebeck.itm.devicedriver;

import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.SerialPort;

/**
 * Standard interface for all iSense devices.
 * All method in this interface has to be implemented as blocking. 
 * So the whole device can run in a single thread.
 * 
 * @author Malte Legenhausen
 */
public interface iSenseDevice {

	/**
	 * Returns the wireless channels under which the device is reachable.
	 * 
	 * @return An array with all channels.
	 */
	int[] getChannels();
	
	/**
	 * Returns the <code>SerialPort</code> instance.
	 * 
	 * @return SerialPort instance.
	 */
	SerialPort getSerialPort();
	
	/**
	 * Programms a iSense device with the given binaryImage without removing the current MAC address.
	 * 
	 * @param binaryImage The image that has to be flashed on the device.
	 * @param monitor A callback interface that is called during the flashing operation.
	 */
	void program(byte[] binaryImage, Monitor monitor);
	
	/**
	 * Remove all data from the flash memory.
	 */
	void eraseFlash();
	
	/**
	 * Write a given amount of bytes to the given address in the flash memory.
	 * 
	 * @param address The address where the data has to be written.
	 * @param data The data that has to be written.
	 * @param length The amount of bytes that has to be wirtten.
	 * @param monitor A callback interface that is called during the flash operation.
	 */
	void writeFlash(int address, byte[] data, int length, Monitor monitor);
	
	/**
	 * Reads a given amount of bytes from the given address.
	 * 
	 * @param address The address from where the bytes has to be read.
	 * @param length The amount of data that has to be readed.
	 * @return The readed bytes.
	 */
	byte[] readFlash(int address, int length);
	
	/**
	 * Read the MAC address from the connected iSense device.
	 * 
	 * @return A <code>MacAddress</code> object representing the mac address of the device.
	 */
	MacAddress readMac();
	
	/**
	 * Writes the MAC address to the connected iSense device.
	 * 
	 * @param macAddress A <code>MacAddress</code> object representing the new mac address of the device.
	 */
	void writeMac(MacAddress macAddress);
	
	/**
	 * Restart the connected iSense device.
	 */
	void reset();
	
	/**
	 * Sends the <code>MessagePacket</code> to the connected iSense device.
	 * 
	 * @param packet The <code>MessagePacket</code> that has to be send to the device.
	 */
	void send(MessagePacket packet);
	
	/**
	 * Receive a <code>MessagePacket</code> from the connected iSense device.
	 * 
	 * @return The received <code>MessagePacket</code>
	 */
	MessagePacket receive();
	
	/**
	 * Returns the <code>InputStream</code> of the connected iSense device.
	 * 
	 * @return The <code>InputStream</code> of the connected iSense device.
	 */
	InputStream getInputStream();
	
	/**
	 * Returns the <code>OutputStream</code> of the connected iSense device.
	 * 
	 * @return The <code>OutputStream</code> of the connected iSense device.
	 */
	OutputStream getOutputStream();
	
	/**
	 * Stores an handler that will be called when a given type occure.
	 * 
	 * @param types The types that specify when the handler is called.
	 * @param handler The handler that will be called.
	 */
	void registerPacketHandler(PacketTypes[] types, PacketHandler handler);
	
	/**
	 * Remove the given handler from the handler list.
	 * 
	 * @param handler The handler that has to be removed.
	 */
	void removePacketHandler(PacketHandler handler);
}
