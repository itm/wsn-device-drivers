package eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbee802154.request;

import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrameType;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeAbstractRequest;

/**
 * @author TLMAT UC
 */
public class XBee802154Request extends XBeeAbstractRequest {

	public static final int DISABLE_ACK = 0x01;
	public static final int SEND_WITH_BROADCAST_PAN_ID = 0x04;

	private final int options;

	public XBee802154Request(int nodeID, int options, byte[] payload) {
		super(nodeID, XBeeFrameType.TRANSMIT_REQUEST_802154, payload);
		this.options = 0x00 | options;
	}

	public XBee802154Request(int nodeID, byte[] payload) {
		this(nodeID, 0x00, payload);
	}

	public int getOptions() {
		return options;
	}

}
