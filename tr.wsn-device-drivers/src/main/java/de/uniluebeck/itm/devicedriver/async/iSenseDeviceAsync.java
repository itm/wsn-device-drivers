package de.uniluebeck.itm.devicedriver.async;

import java.io.InputStream;
import java.io.OutputStream;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.PacketHandler;
import de.uniluebeck.itm.devicedriver.PacketTypes;


/**
 * Async definition of the <code>iSenseDevice</code> interface.
 * 
 * @author Malte Legenhausen
 */
public interface iSenseDeviceAsync {

	/**
	 * Returns the wireless channels under which the device is reachable.
	 * Has to be implemented as short running operation.
	 * 
	 * @return An array with all channels.
	 */
	int[] getChannels();
	
	/**
	 * Programms a iSense device with the given binaryImage without removing the current MAC address.
	 * 
	 * @param binaryImage The image that has to be flashed on the device.
	 * @param monitor A callback interface that is called during the flashing operation.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<Void> program(byte[] binaryImage, AsyncCallback<Void> callback);
	
	/**
	 * Remove all data from the flash memory.
	 * 
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<Void> eraseFlash(AsyncCallback<Void> callback);
	
	/**
	 * Write a given amount of bytes to the given address in the flash memory.
	 * 
	 * @param address The address where the data has to be written.
	 * @param data The data that has to be written.
	 * @param length The amount of bytes that has to be wirtten.
	 * @param monitor A callback interface that is called during the flash operation.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<Void> writeFlash(int address, byte[] data, int length, AsyncCallback<Void> callback);
	
	/**
	 * Reads a given amount of bytes from the given address.
	 * 
	 * @param address The address from where the bytes has to be read.
	 * @param length The amount of data that has to be readed.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<byte[]> readFlash(int address, int length, AsyncCallback<byte[]> callback);
	
	/**
	 * Read the MAC address from the connected iSense device.
	 * 
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<MacAddress> readMac(AsyncCallback<MacAddress> callback);
	
	/**
	 * Writes the MAC address to the connected iSense device.
	 * 
	 * @param macAddress A <code>MacAddress</code> object representing the new mac address of the device.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<Void> writeMac(MacAddress macAddress, AsyncCallback<Void> callback);
	
	/**
	 * Restart the connected iSense device.
	 * 
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<Void> reset(AsyncCallback<Void> callback);
	
	/**
	 * Sends the <code>MessagePacket</code> to the connected iSense device.
	 * 
	 * @param packet The <code>MessagePacket</code> that has to be send to the device.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<Void> send(MessagePacket packet, AsyncCallback<Void> callback);
	
	/**
	 * Receive a <code>MessagePacket</code> from the connected iSense device.
	 * 
	 * @param callback Interface that is called on successfully or failed method execution.
	 */
	void receive(AsyncCallback<MessagePacket> callback);
	
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
	 * Add an handler that will be called when one of the given <code>PacketTypes</code> occure.
	 * 
	 * @param types The types that specify when the handler is called.
	 * @param handler The handler that will be called.
	 */
	void registerPacketHandler(PacketHandler handler, PacketTypes... types);
	
	/**
	 * Add an handler that will be called when the given byte types occure.
	 * 
	 * @param handler
	 * @param types
	 */
	void registerPacketHandler(PacketHandler handler, byte... types);
	
	/**
	 * Remove the given handler from the handler list.
	 * 
	 * @param handler The handler that has to be removed.
	 */
	void removePacketHandler(PacketHandler handler);
}
