package de.uniluebeck.itm.wsn.drivers.core.operation;


/**
 * An interface that has to be used when implementing a program operation.
 * 
 * @author Malte Legenhausen
 */
public interface ProgramOperation extends OperationRunnable<Void> {

	/**
	 * Setter for the binary image that has to be programed.
	 * 
	 * @param data The binary image as byte array.
	 */
	void setBinaryImage(byte[] data);
}
