package de.uniluebeck.itm.devicedriver.operation;

import de.uniluebeck.itm.devicedriver.DeviceBinFile;


public abstract class AbstractProgramOperation extends AbstractOperation<Void> implements ProgramOperation {

	protected DeviceBinFile binaryImage;
	
	@Override
	public void setBinaryImage(DeviceBinFile binaryImage) {
		this.binaryImage = binaryImage;
	}
	
	public DeviceBinFile getBinaryImage() {
		return binaryImage;
	}
}
