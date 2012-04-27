package de.uniluebeck.itm.wsn.drivers.core.operation;


import com.google.common.util.concurrent.TimeLimiter;

import javax.annotation.Nullable;

/**
 * Abstract operation that stores the address and the length internally.
 *
 * @author Malte Legenhausen
 */
public abstract class AbstractReadFlashOperation extends TimeLimitedOperation<byte[]> implements ReadFlashOperation {

	/**
	 * The start address.
	 */
	private final int address;

	/**
	 * The length of the area that has to be read.
	 */
	private final int length;

	protected AbstractReadFlashOperation(final TimeLimiter timeLimiter,
										 final int address,
										 final int length,
										 final long timeout,
										 @Nullable final OperationListener<byte[]> operationCallback) {
		super(timeLimiter, timeout, operationCallback);
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
