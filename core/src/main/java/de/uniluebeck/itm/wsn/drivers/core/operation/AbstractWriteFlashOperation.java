package de.uniluebeck.itm.wsn.drivers.core.operation;


import com.google.common.util.concurrent.TimeLimiter;

import javax.annotation.Nullable;

/**
 * Abstract operation for flashing the device.
 * This class stores the start address, the binary image as byte array and the length of the image.
 * The variables can be accessed by getter methods.
 *
 * @author Malte Legenhausen
 */
public abstract class AbstractWriteFlashOperation extends TimeLimitedOperation<Void> implements WriteFlashOperation {

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

	protected AbstractWriteFlashOperation(final TimeLimiter timeLimiter, int address, byte[] data, int length,
										  final long timeout,
										  @Nullable final OperationListener<Void> operationCallback) {
		super(timeLimiter, timeout, operationCallback);
		this.address = address;
		this.data = data;
		this.length = length;
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
