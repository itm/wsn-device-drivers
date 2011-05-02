package de.uniluebeck.itm.wsn.drivers.core;

import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.SendOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteMacAddressOperation;

/**
 * Standard interface for all devices.
 * All create methods have to return a new operation instance.
 * 
 * @author Malte Legenhausen
 * 
 * @param <C> The connection type that is used by this device.
 */
public interface Device<C extends Connection> extends MessageObserverable {
	
	/**
	 * Returns the <code>Connection</code> object for this device.
	 * 
	 * @return The connection which is used by this device to communicate with the real device.
	 */
	C getConnection();

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
	 * @return The operation for programming the device.
	 */
	ProgramOperation createProgramOperation();
	
	/**
	 * Create a operation that remove all data from the flash memory of the device.
	 * 
	 * @return The operation for erasing the whole flash of the device.
	 */
	EraseFlashOperation createEraseFlashOperation();
	
	/**
	 * Write a given amount of bytes to the given address in the flash memory.
	 * 
	 * @return The operation for writing in the flash of the device.
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
	 * @return The operation for writing a new mac address.
	 */
	WriteMacAddressOperation createWriteMacAddressOperation();
	
	/**
	 * Restart the connected iSense device.
	 * 
	 * @return The operation for resetting the device.
	 */
	ResetOperation createResetOperation();
	
	/**
	 * Sends the <code>MessagePacket</code> to the connected iSense device.
	 * 
	 * @return The operation for sending a message packet to the device.
	 */
	SendOperation createSendOperation();
}
