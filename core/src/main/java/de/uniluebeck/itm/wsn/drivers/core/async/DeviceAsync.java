package de.uniluebeck.itm.wsn.drivers.core.async;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.io.HasInputStream;
import de.uniluebeck.itm.wsn.drivers.core.io.HasOutputStream;


/**
 * Async definition of the <code>Device</code> interface.
 * The monitoring of an operation is moved in the <code>AsyncCallback</code>.
 * Note that an operation timeout will cause a <code>TimeoutException</code>.
 * 
 * @author Malte Legenhausen
 */
public interface DeviceAsync extends HasInputStream, HasOutputStream {
	
	/**
	 * Returns the chip type of this device.
	 * 
	 * @param timeout Maximum operation time before the method will be canceled in milliseconds.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<ChipType> getChipType(long timeout, AsyncCallback<ChipType> callback);
	
	/**
	 * Programms a iSense device with the given binaryImage without removing the current MAC address.
	 * 
	 * @param data The image that has to be flashed on the device.
	 * @param timeout Maximum operation time before the method will be canceled in milliseconds.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<Void> program(byte[] data, long timeout, AsyncCallback<Void> callback);
	
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
	 * @param message The <code>MessagePacket</code> that has to be send to the device.
	 * @param timeout Maximum operation time before the method will be canceled milliseconds.
	 * @param callback Interface that is called on successfully or failed method execution.
	 * @return Returns a <code>OperationHandle</code> for controlling the async operation.
	 */
	OperationHandle<Void> send(byte[] message, long timeout, AsyncCallback<Void> callback);
}
