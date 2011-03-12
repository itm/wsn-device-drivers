package de.uniluebeck.itm.devicedriver.operation;


/**
 * Abstract implementation of a program operation.
 * The binary image will be stored as byte arraw.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractProgramOperation extends AbstractOperation<Void> implements ProgramOperation {

	/**
	 * The image that has to be flashed.
	 */
	private byte[] binaryImage;
	
	@Override
	public void setBinaryImage(final byte[] binaryImage) {
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
