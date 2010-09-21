package de.uniluebeck.itm.devicedriver.operation;

import de.uniluebeck.itm.devicedriver.DeviceBinData;


public abstract class AbstractProgramOperation extends AbstractOperation<Void> implements ProgramOperation {

	protected DeviceBinData binaryImage;
	
	@Override
	public void setBinaryImage(DeviceBinData binaryImage) {
		this.binaryImage = binaryImage;
	}
	
	public DeviceBinData getBinaryImage() {
		return binaryImage;
	}
}
