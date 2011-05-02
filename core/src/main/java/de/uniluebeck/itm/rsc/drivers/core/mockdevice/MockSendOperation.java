package de.uniluebeck.itm.rsc.drivers.core.mockdevice;

import de.uniluebeck.itm.rsc.drivers.core.MessagePacket;
import de.uniluebeck.itm.rsc.drivers.core.operation.SendOperation;


/**
 * Sending a <code>MessagePacket</code> to the device.
 * 
 * @author Malte Legenhausen
 */
public class MockSendOperation extends AbstractMockOperation<Void> implements SendOperation {
	
	/**
	 * The <code>MessagePacket</code> that has to be send.
	 */
	private MessagePacket messagePacket;
	
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
	public void setMessagePacket(final MessagePacket messagePacket) {
		this.messagePacket = messagePacket;
	}
	
	@Override
	public Void returnResult() {
		connection.sendMessage(new String(messagePacket.getContent()));
		return null;
	}
}
