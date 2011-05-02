package de.uniluebeck.itm.wsn.drivers.core.mockdevice;

import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;


/**
 * The operation for programming the <code>MockDevice</code>.
 * 
 * @author Malte Legenhausen
 */
public class MockProgramOperation extends AbstractMockOperation<Void> implements ProgramOperation {
	
	/**
	 * The binarry image that has to be written to the device.
	 */
	private byte[] binaryImage;
	
	/**
	 * The configuration that will store the binary image.
	 */
	private MockConfiguration configuration;
	
	/**
	 * Constructor.
	 * 
	 * @param configuration The configuration of the <code>MockDevice</code>.
	 */
	public MockProgramOperation(final MockConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public Void returnResult() {
		System.arraycopy(binaryImage, 0, configuration.getFlashRom(), 0, binaryImage.length);
		return null;
	}

	@Override
	public void setBinaryImage(final byte[] data) {
		this.binaryImage = data;
	}

}
