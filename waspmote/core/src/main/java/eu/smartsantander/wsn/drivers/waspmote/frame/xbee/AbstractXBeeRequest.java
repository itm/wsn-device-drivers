package eu.smartsantander.wsn.drivers.waspmote.frame.xbee;


import de.uniluebeck.itm.wsn.drivers.core.util.ArrayUtils;

public abstract class AbstractXBeeRequest extends AbstractXBeeFrame {

	private final byte[] payload;

	public AbstractXBeeRequest(int nodeID, XBeeFrameType frameType, byte[] payload) {
		super(nodeID, frameType);
		this.payload = payload;
	}

	public byte[] getPayload() {
		return ArrayUtils.clone(payload);
	}

}
