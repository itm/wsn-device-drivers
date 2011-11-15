package eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbee802154.response;

import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrameType;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeAbstractResponse;

/**
 * @author TLMAT UC
 */
public class XBee802154Response extends XBeeAbstractResponse {

	private static final int ADDRESS_BROADCAST = 0x01;
	private static final int PAN_BROADCAST = 0x02;

	private final int RSSI;
	private final byte options;

	public XBee802154Response(int nodeID, int RSSI, byte options, byte[] payload) {
		super(nodeID, XBeeFrameType.RECEIVE_PACKET_802154, payload);
		this.RSSI = RSSI;
		this.options = options;
	}

	public int getRSSI() {
		return this.RSSI;
	}

	public boolean getAddressBroadcastOption() {
		return ((options & ADDRESS_BROADCAST) == ADDRESS_BROADCAST);
	}

	public boolean getPanBroadcasOption() {
		return ((options & PAN_BROADCAST) == PAN_BROADCAST);
	}

}
