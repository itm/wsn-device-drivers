package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadMacAddressOperation;

public class MockReadMacAddress extends AbstractOperation<MacAddress> implements ReadMacAddressOperation {
	
	private final MacAddress macAddress;
	
	public MockReadMacAddress(MacAddress macAddress) {
		this.macAddress = macAddress;
	}
	
	@Override
	public MacAddress execute(Monitor monitor) throws Exception {
		for(int i = 1; i <= 10 && !isCanceled(); ++i) {
			Thread.sleep(100 * i);
			monitor.onProgressChange(0.1f * i);
		}
		return macAddress;
	}
}
