package de.uniluebeck.itm.wsn.drivers.mock;

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
	private byte[] message;
	
	/**
	 * The <code>MockConnection</code> which is used for sending the message.
	 */
	private final MockDevice device;
	
	/**
	 * Constructor.
	 * 
	 * @param device The <code>MockConnection</code> which is used for sending the message.
	 */
	public MockSendOperation(final MockDevice device) {
		this.device = device;
	}

	@Override
	public void setMessage(final byte[] message) {
		this.message = message;
	}
	
	@Override
	public Void returnResult() {
		device.sendMessage(message);
		return null;
	}
}
