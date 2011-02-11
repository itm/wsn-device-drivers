package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractWriteFlashOperation;

public class MockWriteFlashOperation extends AbstractWriteFlashOperation {

	private static final int STEPS = 10;
	
	private static final int SLEEP = 500;
	
	private final MockConfiguration configuration;
	
	public MockWriteFlashOperation(final MockConfiguration flashRom) {
		this.configuration = flashRom;
	}
	
	@Override
	public Void execute(final Monitor monitor) throws Exception {
		for(int i = 1; i <= STEPS && !isCanceled(); ++i) {
			Thread.sleep(SLEEP);
			final float progress = 0.1f * i;
			monitor.onProgressChange(progress);
		}
		System.arraycopy(getData(), 0, configuration.getFlashRom(), getAddress(), getLength());
		return null;
	}

}
