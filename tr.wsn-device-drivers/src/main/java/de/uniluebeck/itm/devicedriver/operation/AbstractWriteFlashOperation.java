package de.uniluebeck.itm.devicedriver.operation;


public abstract class AbstractWriteFlashOperation extends AbstractOperation<Void> implements WriteFlashOperation {

	private int address;
	
	private byte[] data;
	
	private int length;
	
	@Override
	public void setData(final int address, final byte[] data, final int length) {
		this.address = address;
		this.data = data;
		this.length = length;
	}
	
	public int getAddress() {
		return address;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public int getLength() {
		return length;
	}
}
