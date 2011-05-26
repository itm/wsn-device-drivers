package es.unican.tlmat.wsn.drivers.waspmote.multiplexer;

/**
 * @author TLMAT UC
 */
public class OperationAddressInfo {

	private final int nodeID;
	private final Thread threadID;

	/**
	 * @param nodeID
	 * @param threadID
	 */
	public OperationAddressInfo(int nodeID, Thread threadID) {
		this.nodeID = nodeID;
		this.threadID = threadID;
	}

	public int getNodeID() {
		return nodeID;
	}

	public Thread getThreadID() {
		return threadID;
	}

}
