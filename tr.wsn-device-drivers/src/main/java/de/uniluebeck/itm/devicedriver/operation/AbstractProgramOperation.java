package de.uniluebeck.itm.devicedriver.operation;


public abstract class AbstractProgramOperation extends AbstractOperation<Void> implements ProgramOperation {

	protected byte[] binaryImage;
	
	@Override
	public void setBinaryImage(byte[] binaryImage) {
		this.binaryImage = binaryImage;
	}
	
	public byte[] getBinaryImage() {
		return binaryImage;
	}
}
