package de.uniluebeck.itm.wsn.drivers.core.operation;

/**
 * An interface for an operation that writes data to the flash memory.
 * 
 * @author Malte Legenhausen
 */
public interface WriteFlashOperation extends OperationRunnable<Void> {

	/**
	 * Method for defining the data that has to be written to the flash memory.
	 * 
	 * @param address The address where the data has to be written.
	 * @param data The data that has to be written.
	 * @param length The amount of bytes that has to be wirtten.
	 */
	void setData(int address, byte[] data, int length);
}
