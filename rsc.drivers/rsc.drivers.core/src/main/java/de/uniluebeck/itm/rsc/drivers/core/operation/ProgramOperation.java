package de.uniluebeck.itm.rsc.drivers.core.operation;


/**
 * An interface that has to be used when implementing a program operation.
 * 
 * @author Malte Legenhausen
 */
public interface ProgramOperation extends Operation<Void> {

	/**
	 * Setter for the binary image that has to be programed.
	 * 
	 * @param data The binary image as byte array.
	 */
	void setBinaryImage(byte[] data);
}
