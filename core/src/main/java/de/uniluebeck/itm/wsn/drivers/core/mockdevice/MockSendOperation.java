package de.uniluebeck.itm.wsn.drivers.core.mockdevice;

import de.uniluebeck.itm.wsn.drivers.core.operation.SendOperation;


/**
 * Sending a <code>MessagePacket</code> to the device.
 * 
 * @author Malte Legenhausen
 */
public class MockSendOperation extends AbstractMockOperation<Void> implements SendOperation {
	
	/**
	 * The <code>MessagePacket</code> that has to be send.
	 */
	private byte[] messagePacket;
	
	/**
	 * The <code>MockConnection</code> which is used for sending the message.
	 */
	private final MockConnection connection;
	
	/**
	 * Constructor.
	 * 
	 * @param connection The <code>MockConnection</code> which is used for sending the message.
	 */
	public MockSendOperation(final MockConnection connection) {
		this.connection = connection;
	}

	@Override
	public void setMessage(final byte[] messagePacket) {
		this.messagePacket = messagePacket;
	}
	
	@Override
	public Void returnResult() {
		connection.sendMessage(new String(messagePacket));
		return null;
	}
}
