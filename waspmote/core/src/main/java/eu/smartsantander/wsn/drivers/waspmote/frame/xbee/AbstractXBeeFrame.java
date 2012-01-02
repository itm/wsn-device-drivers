package eu.smartsantander.wsn.drivers.waspmote.frame.xbee;

import de.uniluebeck.itm.wsn.drivers.core.util.DoubleByte;

/**
 * @author TLMAT UC
 */
public abstract class AbstractXBeeFrame {

	private final DoubleByte nodeID;
	private final XBeeFrameType frameType;

	/**
	 * @param nodeID
	 * @param frameType
	 */
	public AbstractXBeeFrame(int nodeID, XBeeFrameType frameType) {
		this.nodeID = new DoubleByte(nodeID);
		this.frameType = frameType;
	}

    public DoubleByte getNodeID() {
		return nodeID;
	}

	public int getNodeID16BitValue() {
		return nodeID.get16BitValue();
	}

	public XBeeFrameType getFrameType() {
		return frameType;
	}

}
