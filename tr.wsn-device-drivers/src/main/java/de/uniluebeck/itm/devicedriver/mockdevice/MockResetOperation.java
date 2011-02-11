package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.ResetOperation;

public class MockResetOperation extends AbstractOperation<Void> implements ResetOperation {

	private static final int SLEEP_TIME = 200;
	
	private static final int RESET_TIME = 1000;
	
	private final MockConnection connection;
	
	public MockResetOperation(final MockConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Void execute(final Monitor monitor) throws Exception {
		Thread.sleep(SLEEP_TIME);
		connection.stopAliveRunnable();
		Thread.sleep(RESET_TIME);
		connection.sendMessage("Booting MockDevice...");
		Thread.sleep(SLEEP_TIME);
		connection.scheduleAliveRunnable();
		return null;
	}
}
