package de.uniluebeck.itm.devicedriver.async;

import java.io.InputStream;
import java.io.OutputStream;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.PacketType;


/**
 * Async definition of the <code>Device</code> interface.
 * The monitoring of an operation is moved in the <code>AsyncCallback</code>.
 * Note that an operation timeout will cause a <code>TimeoutException</code>.
 * 
 * @author Malte Legenhausen
 */
public interface DeviceAsync {
	
	/**
	 * Programms a iSense device with the given binaryImage without removing the current MAC address.
	 * 
	 * @param binaryImage The image that has to be flashed on the device.
	 * @param monitor A callback interface that is called during the flashing operation.
	 * @param timeout Maximum operation time before the method will be canceled in milliseconds.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<Void> program(byte[] binaryImage, long timeout, AsyncCallback<Void> callback);
	
	/**
	 * Remove all data from the flash memory.
	 * 
	 * @param timeout Maximum operation time before the method will be canceled in milliseconds.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<Void> eraseFlash(long timeout, AsyncCallback<Void> callback);
	
	/**
	 * Write a given amount of bytes to the given address in the flash memory.
	 * 
	 * @param address The address where the data has to be written.
	 * @param data The data that has to be written.
	 * @param length The amount of bytes that has to be wirtten.
	 * @param timeout Maximum operation time before the method will be canceled in milliseconds.
	 * @param monitor A callback interface that is called during the flash operation.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<Void> writeFlash(int address, byte[] data, int length, long timeout, AsyncCallback<Void> callback);
	
	/**
	 * Reads a given amount of bytes from the given address.
	 * 
	 * @param address The address from where the bytes has to be read.
	 * @param length The amount of data that has to be readed.
	 * @param timeout Maximum operation time before the method will be canceled in milliseconds.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<byte[]> readFlash(int address, int length, long timeout, AsyncCallback<byte[]> callback);
	
	/**
	 * Read the MAC address from the connected iSense device.
	 * 
	 * @param timeout Maximum operation time before the method will be canceled in milliseconds.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<MacAddress> readMac(long timeout, AsyncCallback<MacAddress> callback);
	
	/**
	 * Writes the MAC address to the connected iSense device.
	 * 
	 * @param macAddress A <code>MacAddress</code> object representing the new mac address of the device.
	 * @param timeout Maximum operation time before the method will be canceled in milliseconds.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<Void> writeMac(MacAddress macAddress, long timeout, AsyncCallback<Void> callback);
	
	/**
	 * Restart the connected iSense device.
	 * 
	 * @param timeout Maximum operation time before the method will be canceled in milliseconds.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<Void> reset(long timeout, AsyncCallback<Void> callback);
	
	/**
	 * Sends the <code>MessagePacket</code> to the connected iSense device.
	 * 
	 * @param packet The <code>MessagePacket</code> that has to be send to the device.
	 * @param timeout Maximum operation time before the method will be canceled milliseconds.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<Void> send(MessagePacket packet, long timeout, AsyncCallback<Void> callback);
	
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
	 * @param listener The listener that will be called.
	 * @param types The types that specify when the listener is called.
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
	 * @param listener The handler that has to be removed.
	 */
	void removeMessagePacketListener(MessagePacketListener listener);
}
