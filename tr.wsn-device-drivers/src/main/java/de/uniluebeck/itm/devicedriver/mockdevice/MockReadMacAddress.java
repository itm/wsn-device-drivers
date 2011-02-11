package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadMacAddressOperation;

public class MockReadMacAddress extends AbstractOperation<MacAddress> implements ReadMacAddressOperation {

	private static final int STEPS = 10;
	
	private static final int SLEEP = 100;
	
	private final MockConfiguration configuration;
	
	public MockReadMacAddress(final MockConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public MacAddress execute(final Monitor monitor) throws Exception {
		for(int i = 1; i <= STEPS && !isCanceled(); ++i) {
			Thread.sleep(SLEEP);
			final float progress = 0.1f * i;
			monitor.onProgressChange(progress);
		}
		return configuration.getMacAddress();
	}
}
