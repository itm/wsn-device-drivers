package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

/**
 * @author TLMAT UC
 */
public class OperationAddressInfo {

	private final int nodeID;
	private final WaspmoteSubchannel subchannel;

	/**
	 * @param nodeID
	 * @param subchannel
	 */
	public OperationAddressInfo(int nodeID, WaspmoteSubchannel subchannel) {
		this.nodeID = nodeID;
		this.subchannel = subchannel;
	}

	public int getNodeID() {
		return nodeID;
	}

	public WaspmoteSubchannel getSubchannel() {
		return subchannel;
	}

}
