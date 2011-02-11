package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractProgramOperation;

public class MockProgramOperation extends AbstractProgramOperation {

	private static final int STEPS = 10;
	
	private static final int SLEEP = 500;
	
	private MockConfiguration configuration;
	
	public MockProgramOperation(final MockConfiguration flashRom) {
		this.configuration = flashRom;
	}
	
	@Override
	public Void execute(final Monitor monitor) throws Exception {
		for(int i = 1; i <= STEPS && !isCanceled(); ++i) {
			Thread.sleep(SLEEP);
			final float progress = 0.1f * i;
			monitor.onProgressChange(progress);
		}
		System.arraycopy(getBinaryImage(), 0, configuration.getFlashRom(), 0, getBinaryImage().length);
		return null;
	}

}
