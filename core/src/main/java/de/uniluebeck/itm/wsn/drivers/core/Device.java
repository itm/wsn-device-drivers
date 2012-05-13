package de.uniluebeck.itm.wsn.drivers.core;

import de.uniluebeck.itm.wsn.drivers.core.io.HasInputStream;
import de.uniluebeck.itm.wsn.drivers.core.io.HasOutputStream;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationFuture;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;

import javax.annotation.Nullable;


/**
 * Async definition of the <code>Device</code> interface.
 * The monitoring of an operation is moved in the <code>OperationListener</code>.
 * Note that an operation timeout will cause a <code>TimeoutException</code>.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public interface Device extends HasInputStream, HasOutputStream, Connectable {

	/**
	 * Returns the chip type of this device.
	 *
	 * @param timeoutMillis
	 * 		Maximum operation time before the method will be canceled in milliseconds.
	 * @param listener
	 * 		Interface that is called on successfully or failed method execution.
	 *
	 * @return Returns a <code>OperationFuture</code> for controlling the async operation.
	 */
	OperationFuture<ChipType> getChipType(long timeoutMillis, @Nullable OperationListener<ChipType> listener);

	/**
	 * Checks if the node is alive.
	 * <p/>
	 * For wired devices this operation call defaults to calling {@link de.uniluebeck.itm.wsn.drivers.core.Device#isConnected()}.
	 * For wireless devices however, this operation call may result in messages being sent and received over the wireless
	 * connection. Please note that this communication may take a while, hence the timeout parameter.
	 *
	 * @param timeoutMillis
	 * 		Maximum operation time before the method will be canceled in milliseconds.
	 * @param listener
	 * 		Interface that is called on successfully or failed method execution.
	 *
	 * @return Returns a <code>OperationFuture</code> for controlling the async operation.
	 */
	OperationFuture<Boolean> isNodeAlive(long timeoutMillis, @Nullable OperationListener<Boolean> listener);

	/**
	 * Programs a iSense device with the given binaryImage without removing the current MAC address.
	 *
	 * @param data
	 * 		The image that has to be flashed on the device.
	 * @param timeoutMillis
	 * 		Maximum operation time before the method will be canceled in milliseconds.
	 * @param listener
	 * 		Interface that is called on successfully or failed method execution.
	 *
	 * @return Returns a <code>OperationFuture</code> for controlling the async operation.
	 */
	OperationFuture<Void> program(byte[] data, long timeoutMillis, @Nullable OperationListener<Void> listener);

	/**
	 * Remove all data from the flash memory.
	 *
	 * @param timeoutMillis
	 * 		Maximum operation time before the method will be canceled in milliseconds.
	 * @param listener
	 * 		Interface that is called on successfully or failed method execution.
	 *
	 * @return Returns a <code>OperationFuture</code> for controlling the async operation.
	 */
	OperationFuture<Void> eraseFlash(long timeoutMillis, @Nullable OperationListener<Void> listener);

	/**
	 * Write a given amount of bytes to the given address in the flash memory.
	 *
	 * @param address
	 * 		The address where the data has to be written.
	 * @param data
	 * 		The data that has to be written.
	 * @param length
	 * 		The amount of bytes that has to be written.
	 * @param timeoutMillis
	 * 		Maximum operation time before the method will be canceled in milliseconds.
	 * @param listener
	 * 		Interface that is called on successfully or failed method execution.
	 *
	 * @return Returns a <code>OperationFuture</code> for controlling the async operation.
	 */
	OperationFuture<Void> writeFlash(int address, byte[] data, int length, long timeoutMillis,
									 @Nullable OperationListener<Void> listener);

	/**
	 * Reads a given amount of bytes from the given address.
	 *
	 * @param address
	 * 		The address from where the bytes has to be read.
	 * @param length
	 * 		The amount of data that has to be read.
	 * @param timeoutMillis
	 * 		Maximum operation time before the method will be canceled in milliseconds.
	 * @param listener
	 * 		Interface that is called on successfully or failed method execution.
	 *
	 * @return Returns a <code>OperationFuture</code> for controlling the async operation.
	 */
	OperationFuture<byte[]> readFlash(int address, int length, long timeoutMillis,
									  @Nullable OperationListener<byte[]> listener);

	/**
	 * Read the MAC address from the connected iSense device.
	 *
	 * @param timeoutMillis
	 * 		Maximum operation time before the method will be canceled in milliseconds.
	 * @param listener
	 * 		Interface that is called on successfully or failed method execution.
	 *
	 * @return Returns a <code>OperationFuture</code> for controlling the async operation.
	 */
	OperationFuture<MacAddress> readMac(long timeoutMillis, @Nullable OperationListener<MacAddress> listener);

	/**
	 * Writes the MAC address to the connected iSense device.
	 *
	 * @param macAddress
	 * 		A <code>MacAddress</code> object representing the new mac address of the device.
	 * @param timeoutMillis
	 * 		Maximum operation time before the method will be canceled in milliseconds.
	 * @param listener
	 * 		Interface that is called on successfully or failed method execution.
	 *
	 * @return Returns a <code>OperationFuture</code> for controlling the async operation.
	 */
	OperationFuture<Void> writeMac(MacAddress macAddress, long timeoutMillis,
								   @Nullable OperationListener<Void> listener);

	/**
	 * Restart the connected iSense device.
	 *
	 * @param timeoutMillis
	 * 		Maximum operation time before the method will be canceled in milliseconds.
	 * @param listener
	 * 		Interface that is called on successfully or failed method execution.
	 *
	 * @return Returns a <code>OperationFuture</code> for controlling the async operation.
	 */
	OperationFuture<Void> reset(long timeoutMillis, @Nullable OperationListener<Void> listener);
}
