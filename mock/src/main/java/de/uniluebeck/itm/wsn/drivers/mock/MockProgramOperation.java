package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.inject.Inject;

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
	@Inject
	public MockProgramOperation(MockConfiguration configuration) {
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
