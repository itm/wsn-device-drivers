package de.uniluebeck.itm.devicedriver;

/**
 * 
 * 
 * @author Malte Legenhausen
 */
public interface PacketHandler {

	/**
	 * 
	 * @param p
	 */
	void receivePacket(MessagePacket p);

	/**
	 * Debug output in plaintext.
	 * 
	 * @param p
	 */
	void receivePlainText(MessagePlainText p);

	/**
	 * 
	 */
	@Deprecated
	void operationCanceled(Operation op);

	/**
	 * 
	 */
	@Deprecated
	void operationDone(Operation op, Object result);

	/**
	 * 
	 */
	@Deprecated
	void operationProgress(Operation op, float fraction);
}
