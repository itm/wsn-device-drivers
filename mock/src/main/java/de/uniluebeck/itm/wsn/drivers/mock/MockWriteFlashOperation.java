package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;


/**
 * Mock operation for writing data in the imulated flash rom.
 * 
 * @author Malte Legenhausen
 */
public class MockWriteFlashOperation extends AbstractMockOperationRunnable<Void> implements WriteFlashOperation {
	
	/**
	 * The sleep time for a write operation.
	 */
	private final static int SLEEP = 500;
	
	/**
	 * Start address for writing in the flash.
	 */
	private int address;
	
	/**
	 * The data that has to be written.
	 */
	private byte[] data;
	
	/**
	 * The length of the data.
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
	@Inject
	public MockWriteFlashOperation(MockConfiguration configuration) {
		super(SLEEP, DEFAULT_STEPS);
		this.configuration = configuration;
	}
	
	@Override
	public Void returnResult() {
		System.arraycopy(data, 0, configuration.getFlashRom(), address, length);
		return null;
	}

	@Override
	public void setData(int address, byte[] data, int length) {
		this.address = address;
		this.data = data;
		this.length = length;
	}
}
