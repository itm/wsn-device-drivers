package eu.smartsantander.wsn.drivers.waspmote.frame.xbee;

import de.uniluebeck.itm.wsn.drivers.core.util.DoubleByte;

/**
 * @author TLMAT UC
 */
public abstract class XBeeFrame {

	public static final int PROTOCOL_802_15_4 = 0x01;
	public static final int PROTOCOL_DIGIMESH = 0x02;
	public static final int PROTOCOL_UNKNOWN = 0x03;

	public static final int TRANSMIT_REQUEST_DIGI = 0x10;
	public static final int TRANSMIT_STATUS_DIGI = 0x8B;
	public static final int RECEIVE_PACKET_DIGI = 0x90;

	public static final int TRANSMIT_REQUEST_802_15_4 = 0x00;
	public static final int TRANSMIT_STATUS_802_15_4 = 0x89;
	public static final int RECEIVE_PACKET_802_15_4 = 0x80;

	private final DoubleByte nodeID;
    private final int protocol;
	private final int frameType;

	public static int getProtocol(int frameType) {
		switch (frameType) {
		case XBeeFrame.TRANSMIT_STATUS_DIGI:
		case XBeeFrame.RECEIVE_PACKET_DIGI:
		case XBeeFrame.TRANSMIT_REQUEST_DIGI:
			return XBeeFrame.PROTOCOL_DIGIMESH;

		case XBeeFrame.TRANSMIT_STATUS_802_15_4:
		case XBeeFrame.RECEIVE_PACKET_802_15_4:
		case XBeeFrame.TRANSMIT_REQUEST_802_15_4:
			return XBeeFrame.PROTOCOL_802_15_4;

		default:
			return XBeeFrame.PROTOCOL_UNKNOWN;
		}
	}

	/**
	 * @param nodeID
	 * @param frameType
	 */
	public XBeeFrame(int nodeID, int frameType) {
		this.nodeID = new DoubleByte(nodeID);
		this.frameType = frameType;
		this.protocol = XBeeFrame.getProtocol(frameType);
	}

    public DoubleByte getNodeID() {
		return nodeID;
	}

	public int getNodeID16BitValue() {
		return nodeID.get16BitValue();
	}

	public int getFrameType() {
		return frameType;
	}

	public int getProtocol() {
		return protocol;
	}

}
