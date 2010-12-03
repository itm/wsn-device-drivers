package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractWriteMacAddressOperation;

public class MockWriteMacAddressOperation extends
		AbstractWriteMacAddressOperation {

	private final MockDevice device;
	
	public MockWriteMacAddressOperation(MockDevice device) {
		this.device = device;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		for(int i = 1; i <= 10 && !isCanceled(); ++i) {
			Thread.sleep(100 * i);
			monitor.onProgressChange(0.1f * i);
		}
		device.setMacAddress(macAddress);
		return null;
	}

}
