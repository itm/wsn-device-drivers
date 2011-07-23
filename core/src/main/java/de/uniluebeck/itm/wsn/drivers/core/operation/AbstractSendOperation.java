package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.common.base.Preconditions;



/**
 * Abstract operation for sending a message packet to the device.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractSendOperation implements SendOperation {
	
	/**
	 * The message packet that has to be send.
	 */
	private byte[] message;
	
	@Override
	public void setMessage(final byte[] message) {
		Preconditions.checkArgument(message != null, "A null message is not allowed.");
		this.message = new byte[message.length];
		System.arraycopy(message, 0, this.message, 0, message.length);
	}
	
	/**
	 * Getter for the message packet.
	 * 
	 * @return The message packet.
	 */
	public byte[] getMessage() {
		return message;
	}
}
