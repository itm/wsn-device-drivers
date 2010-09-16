package de.uniluebeck.itm.devicedriver.operation;


public abstract class AbstractWriteFlashOperation extends AbstractOperation<Void> implements WriteFlashOperation {

	protected int address;
	
	protected byte[] data;
	
	protected int length;
	
	@Override
	public void setData(int address, byte[] data, int length) {
		this.address = address;
		this.data = data;
		this.length = length;
	}
}
