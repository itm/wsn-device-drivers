package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.ResetOperation;

public class MockResetOperation extends AbstractOperation<Void> implements
		ResetOperation {

	private MockDevice device;
	
	public MockResetOperation(MockDevice device) {
		this.device = device;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		Thread.sleep(200);
		device.stopAliveRunnable();
		Thread.sleep(1000);
		device.sendLogMessage("Booting MockDevice...");
		Thread.sleep(100);
		device.scheduleAliveRunnable();
		return null;
	}
}
