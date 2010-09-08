package de.uniluebeck.itm.devicedriver.operation;


public abstract class AbstractReadFlashOperation extends AbstractOperation<byte[]> implements ReadFlashOperation {

	protected int address;
	
	protected int length;
	
	@Override
	public void setAddress(int address, int length) {
		this.address = address;
		this.length = length;
	}
}
