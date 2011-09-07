package de.uniluebeck.itm.wsn.drivers.core.operation;


/**
 * Abstract operation for flashing the device.
 * This class stores the start address, the binary image as byte array and the length of the image.
 * The variables can be accessed by getter methods.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractWriteFlashOperation implements WriteFlashOperation {

	/**
	 * The start address in the flash where the given data will be written.
	 */
	private int address;
	
	/**
	 * The data that will be written in the flash.
	 */
	private byte[] data;
	
	/**
	 * The length of the data.
	 */
	private int length;
	
	@Override
	public void setData(final int anAddress, final byte[] aData, final int aLength) {
		address = anAddress;
		data = aData;
		length = aLength;
	}
	
	/**
	 * Getter for the start address.
	 * 
	 * @return The address.
	 */
	public int getAddress() {
		return address;
	}
	
	/**
	 * Getter for the data.
	 * 
	 * @return The data as byte array.
	 */
	public byte[] getData() {
		return data;
	}
	
	/**
	 * The length of the data.
	 * 
	 * @return The length.
	 */
	public int getLength() {
		return length;
	}
}
