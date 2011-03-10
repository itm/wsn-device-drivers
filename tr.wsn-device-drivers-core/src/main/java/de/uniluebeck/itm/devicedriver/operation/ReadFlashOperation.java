package de.uniluebeck.itm.devicedriver.operation;

/**
 * Interface that defines a operation that reads data from the device memory.
 * 
 * @author Malte Legenhausen
 */
public interface ReadFlashOperation extends Operation<byte[]> {

	/**
	 * Sets a address and length for the data that has to be red from the memory.
	 * 
	 * @param address The start address of the data set.
	 * @param length The length of the data set.
	 */
	void setAddress(int address, int length);
}
