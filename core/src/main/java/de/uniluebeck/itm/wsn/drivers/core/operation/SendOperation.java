package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.inject.ImplementedBy;


/**
 * Interface that defines an <code>Operation</code> that sends a <code>MessagePacket</code> to a device.
 * 
 * @author Malte Legenhausen
 */
@ImplementedBy(SimpleSendOperation.class)
public interface SendOperation extends Operation<Void> {

	/**
	 * The <code>MessagePacket</code> that has to be send to the device.
	 * 
	 * @param messagePacket The <code>MessagePacket</code> that has to be send.
	 */
	void setMessage(byte[] messagePacket);
}
