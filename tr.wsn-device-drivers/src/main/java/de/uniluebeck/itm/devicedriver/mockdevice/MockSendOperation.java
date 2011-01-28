package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractSendOperation;

public class MockSendOperation extends AbstractSendOperation {

	private final MockConnection connection;
	
	public MockSendOperation(final MockConnection connection) {
		this.connection = connection;
	}
	
	@Override
	public Void execute(Monitor monitor) throws Exception {
		for (int i = 0; i < 10; ++i) {
			Thread.sleep(50);
			monitor.onProgressChange(0.1f * i);
		}
		connection.sendMessage(new String(messagePacket.getContent()));
		return null;
	}
}
