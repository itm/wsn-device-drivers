package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.operation.ReadFlashOperation;


/**
 * Mock operation for reading data from the imulated flash rom in the configuration.
 * 
 * @author Malte Legenhausen
 */
public class MockReadFlashOperation extends AbstractMockOperation<byte[]> implements ReadFlashOperation {
	
	/**
	 * The start address of the read operation.
	 */
	private int address;
	
	/**
	 * The length of the data that has to be read.
	 */
	private int length;
	
	/**
	 * The <code>MockConfiguration</code> of the <code>MockDevice</code>.
	 */
	private final MockConfiguration configuration;
	
	/**
	 * Constructor.
	 * 
	 * @param configuration The <code>MockConfiguration</code> of the <code>MockDevice</code>.
	 */
	public MockReadFlashOperation(final MockConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public byte[] returnResult() {
		final byte[] result = new byte[length];
		System.arraycopy(configuration.getFlashRom(), address, result, 0, length);
		return result;
	}
	
	@Override
	public void setAddress(final int address, final int length) {
		this.address = address;
		this.length = length;
	}
}
