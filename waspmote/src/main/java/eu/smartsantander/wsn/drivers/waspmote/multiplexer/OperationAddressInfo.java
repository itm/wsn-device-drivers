package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

/**
 * @author TLMAT UC
 */
public class OperationAddressInfo {

	private final int nodeID;
	private final int subchannelID;

	/**
	 * @param nodeID
	 * @param subchannelID
	 */
	public OperationAddressInfo(int nodeID, int subchannelID) {
		this.nodeID = nodeID;
		this.subchannelID = subchannelID;
	}

	public int getNodeID() {
		return nodeID;
	}

	public int getSubchannelID() {
		return subchannelID;
	}

}
