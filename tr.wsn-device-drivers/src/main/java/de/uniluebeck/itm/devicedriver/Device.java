package de.uniluebeck.itm.devicedriver;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Standard interface for all devices.
 * All create methods have to return a new operation instance.
 * 
 * @author Malte Legenhausen
 */
public interface Device {
	
	/**
	 * Returns the <code>Connection</code> object for this device.
	 * 
	 * @return
	 */
	Connection getConnection();

	/**
	 * Returns the wireless channels under which the device is reachable.
	 * 
	 * @return An array with all channels.
	 */
	int[] getChannels();
	
	/**
	 * Create a new operation for determining the <code>ChipType</code>.
	 * 
	 * @return The operation for determing the <code>ChipType</code>.
	 */
	GetChipTypeOperation createGetChipTypeOperation();
	
	/**
	 * Create a program operation for this device with the given binaryImage without removing the current MAC address.
	 * 
	 * @param binaryImage The image that has to be flashed on the device.
	 */
	ProgramOperation createProgramOperation();
	
	/**
	 * Create a operation that remove all data from the flash memory of the device.
	 */
	EraseFlashOperation createEraseFlashOperation();
	
	/**
	 * Write a given amount of bytes to the given address in the flash memory.
	 * 
	 * @return 
	 */
	WriteFlashOperation createWriteFlashOperation();
	
	/**
	 * Reads a given amount of bytes from the given address.
	 * 
	 * @return The readed bytes.
	 */
	ReadFlashOperation createReadFlashOperation();
	
	/**
	 * Read the MAC address from the connected iSense device.
	 * 
	 * @return A <code>MacAddress</code> object representing the mac address of the device.
	 */
	ReadMacAddressOperation createReadMacAddressOperation();
	
	/**
	 * Writes the MAC address to the connected iSense device.
	 * 
	 * @param macAddress A <code>MacAddress</code> object representing the new mac address of the device.
	 */
	WriteMacAddressOperation createWriteMacAddressOperation();
	
	/**
	 * Restart the connected iSense device.
	 */
	ResetOperation createResetOperation();
	
	/**
	 * Sends the <code>MessagePacket</code> to the connected iSense device.
	 * 
	 * @param packet The <code>MessagePacket</code> that has to be send to the device.
	 */
	SendOperation createSendOperation();
	
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
	 * @param listener The listener that will be called.
	 * @param types The types that specify when the handler is called.
	 */
	void addMessagePacketListener(MessagePacketListener listener, PacketType... types);
	
	/**
	 * Add an handler that will be called when the given byte types occure.
	 * 
	 * @param listener The listener that will be called.
	 * @param types The types as byte array that specify when the listener is called.
	 */
	void addMessagePacketListener(MessagePacketListener listener, int... types);
	
	/**
	 * Remove the given handler from the handler list.
	 * 
	 * @param listener The listener that has to be removed.
	 */
	void removeMessagePacketListener(MessagePacketListener listener);
}
