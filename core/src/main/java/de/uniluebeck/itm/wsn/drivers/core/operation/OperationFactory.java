package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.inject.assistedinject.Assisted;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;

import javax.annotation.Nullable;

/**
 * The factory for creating all kinds of operation to run on the device.
 *
 * @author Daniel Bimschas
 */
public interface OperationFactory {

	/**
	 * Creates a new {@link EraseFlashOperation} instance.
	 *
	 * @param timeoutMillis
	 * 		the number of milliseconds the operation is allowed to run before a timeout is assumed
	 * @param listener
	 * 		an {@link OperationListener} instance to indicate operation progress
	 *
	 * @return a newly created {@link EraseFlashOperation} instance
	 */
	EraseFlashOperation createEraseFlashOperation(long timeoutMillis, @Nullable OperationListener<Void> listener);

	/**
	 * Creates a new {@link GetChipTypeOperation} instance.
	 *
	 * @param timeoutMillis
	 * 		the number of milliseconds the operation is allowed to run before a timeout is assumed
	 * @param listener
	 * 		an {@link OperationListener} instance to indicate operation progress
	 *
	 * @return a newly created {@link GetChipTypeOperation} instance
	 */
	GetChipTypeOperation createGetChipTypeOperation(long timeoutMillis, @Nullable OperationListener<ChipType> listener);

	/**
	 * Creates a new {@link ProgramOperation} instance
	 *
	 * @param data
	 * 		The binary image as byte array
	 * @param timeoutMillis
	 * 		the number of milliseconds the operation is allowed to run before a timeout is assumed
	 * @param listener
	 * 		an {@link OperationListener} instance to indicate operation progress
	 *
	 * @return a newly created {@link ProgramOperation} instance
	 */
	ProgramOperation createProgramOperation(byte[] data, long timeoutMillis,
											@Nullable OperationListener<Void> listener);

	/**
	 * Creates a new {@link ReadFlashOperation} instance.
	 *
	 * @param address
	 * 		The start address of the data set.
	 * @param length
	 * 		The length of the data set.
	 * @param timeoutMillis
	 * 		the number of milliseconds the operation is allowed to run before a timeout is assumed
	 * @param listener
	 * 		an {@link OperationListener} instance to indicate operation progress
	 *
	 * @return a newly created {@link ReadFlashOperation} instance
	 */
	ReadFlashOperation createReadFlashOperation(@Assisted("address") int address, @Assisted("length") int length,
												long timeoutMillis, @Nullable OperationListener<byte[]> listener);

	/**
	 * Creates a new {@link ReadMacAddressOperation} instance.
	 *
	 * @param timeoutMillis
	 * 		the number of milliseconds the operation is allowed to run before a timeout is assumed
	 * @param listener
	 * 		an {@link OperationListener} instance to indicate operation progress
	 *
	 * @return a newly created {@link ReadMacAddressOperation} instance
	 */
	ReadMacAddressOperation createReadMacAddressOperation(long timeoutMillis,
														  @Nullable OperationListener<MacAddress> listener);

	/**
	 * Creates a new {@link ResetOperation} instance.
	 *
	 * @param timeoutMillis
	 * 		the number of milliseconds the operation is allowed to run before a timeout is assumed
	 * @param listener
	 * 		an {@link OperationListener} instance to indicate operation progress
	 *
	 * @return a newly created {@link ResetOperation} instance
	 */
	ResetOperation createResetOperation(long timeoutMillis, @Nullable OperationListener<Void> listener);

	/**
	 * Creates a new {@link WriteFlashOperation} instance.
	 *
	 * @param address
	 * 		The address where the data has to be written.
	 * @param data
	 * 		The data that has to be written.
	 * @param length
	 * 		The amount of bytes that has to be written.
	 * @param timeoutMillis
	 * 		the number of milliseconds the operation is allowed to run before a timeout is assumed
	 * @param listener
	 * 		an {@link OperationListener} instance to indicate operation progress
	 *
	 * @return a newly created {@link WriteFlashOperation instance}
	 */
	WriteFlashOperation createWriteFlashOperation(@Assisted("address") int address, byte[] data,
												  @Assisted("length") int length, long timeoutMillis,
												  @Nullable OperationListener<Void> listener);

	/**
	 * Creates a new {@link WriteMacAddressOperation} instance
	 *
	 * @param macAddress
	 * 		The <code>MacAddress</code> that has to be written.
	 * @param timeoutMillis
	 * 		the number of milliseconds the operation is allowed to run before a timeout is assumed
	 * @param listener
	 * 		an {@link OperationListener} instance to indicate operation progress
	 *
	 * @return a newly created {@link WriteMacAddressOperation} instance
	 */
	WriteMacAddressOperation createWriteMacAddressOperation(MacAddress macAddress, long timeoutMillis,
															@Nullable OperationListener<Void> listener);

	/**
	 * Creates a new {@link IsNodeAliveOperation} instance
	 *
	 * @param timeoutMillis
	 * 		the number of milliseconds the operation is allowed to run before a timeout is assumed
	 * @param listener
	 * 		an {@link OperationListener} instance to indicate operation progress
	 *
	 * @return a newly created {@link IsNodeAliveOperation} instance
	 */
	IsNodeAliveOperation createIsNodeAliveOperation(long timeoutMillis, OperationListener<Boolean> listener);
}
