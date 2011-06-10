package eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi;

import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrame;

/**
 * @author TLMAT UC
 */
public class XBeeDigiResponse extends XBeeFrame {

	public static final int PACKET_ACKNOWLEDGED = 0x01;
	public static final int BROADCAST_PACKET = 0x02;

	private final int options;
	private final byte[] payload;

	public XBeeDigiResponse(int nodeID, int options, byte[] payload) {
		super(nodeID, XBeeFrame.RECEIVE_PACKET_DIGI);
		this.options = options;
		this.payload = payload;
	}

	public int getOptions() {
		return options;
	}

	public byte[] getPayload() {
		return payload;
	}

}
