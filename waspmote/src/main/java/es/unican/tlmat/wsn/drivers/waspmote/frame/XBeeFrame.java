package es.unican.tlmat.wsn.drivers.waspmote.frame;

import es.unican.tlmat.util.DoubleByte;
import es.unican.tlmat.util.ExtendedMacAddress;
/**
 * @author TLMAT UC
 */
public abstract class XBeeFrame {

	public static final int PROTOCOL_802_15_4 = 0x01;
	public static final int PROTOCOL_DIGIMESH = 0x02;
	public static final int PROTOCOL_UNKNOWN = 0x03;

	public static final int TRANSMIT_REQUEST = 0x10;
	public static final int TRANSMIT_STATUS = 0x8B;
	public static final int RECEIVE_PACKET = 0x90;

	private  DoubleByte nodeID;
	private  ExtendedMacAddress macAddress;
	/**
     * @return the macAddress
     */
    public ExtendedMacAddress getMacAddress() {
        return macAddress;
    }

    /**
     * @param macAddress the macAddress to set
     */
    public void setMacAddress(ExtendedMacAddress macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * @param nodeID the nodeID to set
     */
    public void setNodeID(DoubleByte nodeID) {
        this.nodeID = nodeID;
    }

    private final int protocol;
	private final int frameType;

	/**
	 * @param nodeID
	 * @param frameType
	 */
	public XBeeFrame(int nodeID, int frameType) {
		this.nodeID = new DoubleByte(nodeID);
		this.frameType = frameType;
		this.protocol = getProtocol(frameType);
	}

	/**
     * @param macAddress
     * @param frameType
     */
    public XBeeFrame(ExtendedMacAddress macAddress, int frameType) {
        this.macAddress = macAddress;
        this.frameType = frameType;
        this.protocol = getProtocol(frameType);
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

	public static int getProtocol(int frameType) {
		switch (frameType) {
		case XBeeFrame.TRANSMIT_STATUS:
		case XBeeFrame.RECEIVE_PACKET:
		case XBeeFrame.TRANSMIT_REQUEST:
			return XBeeFrame.PROTOCOL_DIGIMESH;
		default:
			return XBeeFrame.PROTOCOL_UNKNOWN;
		}
	}

}
