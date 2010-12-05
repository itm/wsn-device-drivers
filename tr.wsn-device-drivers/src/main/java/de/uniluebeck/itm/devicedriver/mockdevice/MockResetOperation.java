package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.ResetOperation;

public class MockResetOperation extends AbstractOperation<Void> implements
		ResetOperation {

	private MockConnection connection;
	
	public MockResetOperation(MockConnection device) {
		this.connection = device;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		Thread.sleep(200);
		connection.stopAliveRunnable();
		Thread.sleep(1000);
		connection.sendMessage("Booting MockDevice...");
		Thread.sleep(100);
		connection.scheduleAliveRunnable();
		return null;
	}
}
