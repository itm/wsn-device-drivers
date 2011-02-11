package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractSendOperation;

public class MockSendOperation extends AbstractSendOperation {

	private static final int STEPS = 10;
	
	private static final int SLEEP = 50;
	
	private final MockConnection connection;
	
	public MockSendOperation(final MockConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Void execute(final Monitor monitor) throws Exception {
		for (int i = 0; i < STEPS; ++i) {
			Thread.sleep(SLEEP);
			final float progress = 0.1f * i;
			monitor.onProgressChange(progress);
		}
		connection.sendMessage(new String(getMessagePacket().getContent()));
		return null;
	}
}
