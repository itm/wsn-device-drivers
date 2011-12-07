package eu.smartsantander.wsn.drivers.waspmote.frame.xbee;


public abstract class XBeeAbstractResponse extends XBeeFrame {

	private final byte[] payload;

	public XBeeAbstractResponse(int nodeID, XBeeFrameType frameType, byte[] payload) {
		super(nodeID, frameType);
		this.payload = payload;
	}

	public byte[] getPayload() {
		return payload;
	}

}
