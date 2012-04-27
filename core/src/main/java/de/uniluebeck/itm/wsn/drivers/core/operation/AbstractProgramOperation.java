package de.uniluebeck.itm.wsn.drivers.core.operation;


import com.google.common.util.concurrent.TimeLimiter;

import javax.annotation.Nullable;

/**
 * Abstract implementation of a program operation.
 * The binary image will be stored as byte arraw.
 *
 * @author Malte Legenhausen
 */
public abstract class AbstractProgramOperation extends TimeLimitedOperation<Void> implements ProgramOperation {

	/**
	 * The image that has to be flashed.
	 */
	private final byte[] binaryImage;

	public AbstractProgramOperation(final TimeLimiter timeLimiter,
									final byte[] binaryImage,
									final long timeoutMillis,
									@Nullable final OperationListener<Void> operationCallback) {
		super(timeLimiter, timeoutMillis, operationCallback);
		this.binaryImage = binaryImage;
	}

	/**
	 * Getter for the binary image.
	 *
	 * @return Binary image as byte array.
	 */
	public byte[] getBinaryImage() {
		return binaryImage;
	}
}
