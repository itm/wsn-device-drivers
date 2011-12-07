package eu.smartsantander.wsn.drivers.waspmote.frame.xbee;


public abstract class XBeeAbstractRequest extends XBeeFrame {

	private final byte[] payload;

	public XBeeAbstractRequest(int nodeID, XBeeFrameType frameType, byte[] payload) {
		super(nodeID, frameType);
		this.payload = payload;
	}

	public byte[] getPayload() {
		return payload;
	}

}
