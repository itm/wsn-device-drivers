package de.uniluebeck.itm.wsn.drivers.core.operation;


/**
 * Abstract operation that stores the address and the length internally.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractReadFlashOperation extends AbstractOperation<byte[]> implements ReadFlashOperation {

	/**
	 * The start address.
	 */
	private int address;
	
	/**
	 * The length of the area that has to be read.
	 */
	private int length;
	
	@Override
	public void setAddress(final int anAddress, final int aLength) {
		address = anAddress;
		length = aLength;
	}
	
	/**
	 * Getter for the start address of the read operation.
	 * 
	 * @return The start address.
	 */
	public int getAddress() {
		return address;
	}
	
	/**
	 * Getter for the length of the segment that has to be read.
	 * 
	 * @return The length of the segment.
	 */
	public int getLength() {
		return length;
	}
}
