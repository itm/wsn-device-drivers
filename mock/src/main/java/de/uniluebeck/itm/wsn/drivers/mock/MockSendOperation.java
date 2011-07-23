package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.SendOperation;


/**
 * Sending a <code>MessagePacket</code> to the connection.
 * 
 * @author Malte Legenhausen
 */
public class MockSendOperation extends AbstractMockOperationRunnable<Void> implements SendOperation {
	
	/**
	 * The <code>MessagePacket</code> that has to be send.
	 */
	private byte[] message;
	
	/**
	 * The <code>MockConnection</code> which is used for sending the message.
	 */
	private final MockConnection connection;
	
	/**
	 * Constructor.
	 * 
	 * @param connection The <code>MockConnection</code> which is used for sending the message.
	 */
	@Inject
	public MockSendOperation(MockConnection connection) {
		this.connection = connection;
	}

	@Override
	public void setMessage(final byte[] message) {
		this.message = message;
	}
	
	@Override
	public Void returnResult() {
		connection.sendMessage(message);
		return null;
	}
}
