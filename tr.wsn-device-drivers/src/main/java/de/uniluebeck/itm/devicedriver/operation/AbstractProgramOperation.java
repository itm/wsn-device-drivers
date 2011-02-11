package de.uniluebeck.itm.devicedriver.operation;


public abstract class AbstractProgramOperation extends AbstractOperation<Void> implements ProgramOperation {

	private byte[] binaryImage;
	
	@Override
	public void setBinaryImage(final byte[] binaryImage) {
		this.binaryImage = binaryImage;
	}
	
	public byte[] getBinaryImage() {
		return binaryImage;
	}
}
