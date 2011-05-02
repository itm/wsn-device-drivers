package de.uniluebeck.itm.rsc.drivers.core.operation;


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
	public void setAddress(final int address, final int length) {
		this.address = address;
		this.length = length;
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
