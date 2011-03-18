package de.uniluebeck.itm.rsc.drivers.core.operation;

import de.uniluebeck.itm.rsc.drivers.core.MessagePacket;


/**
 * Abstract operation for sending a message packet to the device.
 * 
 * @author Malte Legenhausen
 */
public abstract class AbstractSendOperation extends AbstractOperation<Void> implements SendOperation {
	
	/**
	 * The message packet that has to be send.
	 */
	private MessagePacket messagePacket;
	
	@Override
	public void setMessagePacket(final MessagePacket messagePacket) {
		this.messagePacket = messagePacket;
	}
	
	/**
	 * Getter for the message packet.
	 * 
	 * @return The message packet.
	 */
	public MessagePacket getMessagePacket() {
		return messagePacket;
	}
}
