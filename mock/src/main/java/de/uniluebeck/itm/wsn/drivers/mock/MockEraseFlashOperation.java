package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;


/**
 * This operation fills the byte array of the given configuration with 0x00.
 * 
 * @author Malte Legenhausen
 */
public class MockEraseFlashOperation implements EraseFlashOperation {

	/**
	 * Sleeping time between each iteration.
	 */
	private static final int SLEEP = 100;
	
	/**
	 * The configuration that contains the byte array that has to be erased.
	 */
	private final MockConfiguration configuration;
	
	/**
	 * Constructor.
	 * 
	 * @param configuration The configuration that contains the flash rom that has to be erased.
	 */
	@Inject
	public MockEraseFlashOperation(MockConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
		final byte[] flashRom = configuration.getFlashRom();
		final float worked = 1.0f / flashRom.length;
		for (int i = 0; i < flashRom.length; ++i) {
			Thread.sleep(SLEEP);
			flashRom[i] = 0x00;
			progressManager.worked(worked);
		}
		configuration.setFlashRom(flashRom);
		return null;
	}

}
