package de.uniluebeck.itm.devicedriver.operation;


public abstract class AbstractReadFlashOperation extends AbstractOperation<byte[]> implements ReadFlashOperation {

	private int address;
	
	private int length;
	
	@Override
	public void setAddress(final int address, final int length) {
		this.address = address;
		this.length = length;
	}
	
	public int getAddress() {
		return address;
	}
	
	public int getLength() {
		return length;
	}
}
