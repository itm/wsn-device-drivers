package de.uniluebeck.itm.rsc.drivers.core.mockdevice;

import de.uniluebeck.itm.rsc.drivers.core.Monitor;
import de.uniluebeck.itm.rsc.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.rsc.drivers.core.operation.EraseFlashOperation;


/**
 * This operation fills the byte array of the given configuration with 0x00.
 * 
 * @author Malte Legenhausen
 */
public class MockEraseFlashOperation extends AbstractOperation<Void> implements EraseFlashOperation {

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
	public MockEraseFlashOperation(final MockConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public Void execute(final Monitor monitor) throws Exception {
		final byte[] flashRom = configuration.getFlashRom();
		for (int i = 0; i < flashRom.length; ++i) {
			Thread.sleep(SLEEP);
			flashRom[i] = 0x00;
			final float progress = (i * 1.0f) / flashRom.length;
			monitor.onProgressChange(progress);
		}
		configuration.setFlashRom(flashRom);
		return null;
	}

}
