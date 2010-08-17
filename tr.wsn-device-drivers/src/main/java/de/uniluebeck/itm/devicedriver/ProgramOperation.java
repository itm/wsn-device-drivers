package de.uniluebeck.itm.devicedriver;

/**
 * An interface that has to be used when implementing a program operation.
 * 
 * @author Malte Legenhausen
 */
public interface ProgramOperation extends Operation<Void> {

	/**
	 * Setter for the binary image that has to be programed.
	 * 
	 * @param binaryImage The binary image.
	 */
	void setBinaryImage(byte[] binaryImage);
}
