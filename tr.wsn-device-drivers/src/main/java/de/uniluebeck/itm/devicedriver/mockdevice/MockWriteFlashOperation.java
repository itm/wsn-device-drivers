package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractWriteFlashOperation;

public class MockWriteFlashOperation extends AbstractWriteFlashOperation {

	private final MockConfiguration configuration;
	
	public MockWriteFlashOperation(MockConfiguration flashRom) {
		this.configuration = flashRom;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		for(int i = 1; i <= 10 && !isCanceled(); ++i) {
			Thread.sleep(500 * i);
			monitor.onProgressChange(0.1f * i);
		}
		System.arraycopy(data, 0, configuration.getFlashRom(), address, length);
		return null;
	}

}
