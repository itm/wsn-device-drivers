package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractReadFlashOperation;

public class MockReadFlashOperation extends AbstractReadFlashOperation {

	private static final int STEPS = 10;
	
	private static final int SLEEP = 100;
	
	private final MockConfiguration configuration;
	
	public MockReadFlashOperation(final MockConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public byte[] execute(final Monitor monitor) throws Exception {
		for(int i = 1; i <= STEPS && !isCanceled(); ++i) {
			Thread.sleep(SLEEP);
			final float progress = 0.1f * i;
			monitor.onProgressChange(progress);
		}
		final byte[] result = new byte[getLength()];
		System.arraycopy(configuration.getFlashRom(), getAddress(), result, 0, getLength());
		return result;
	}

}
