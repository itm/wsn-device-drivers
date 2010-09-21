package de.uniluebeck.itm.devicedriver.operation;

import de.uniluebeck.itm.devicedriver.DeviceBinData;

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
	void setBinaryImage(DeviceBinData binaryImage);
}
