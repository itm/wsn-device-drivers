package eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi;

import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrame;

/**
 * @author TLMAT UC
 */
public class XBeeDigiStatusResponse extends XBeeFrame {

	public static final int SUCCESS = 0x00;
	public static final int MAC_ACK_FAILURE = 0x01;
	public static final int INVALID_DESTINATION_ENDPOINT = 0x15;
	public static final int NETWORK_ACK_FAILURE = 0x21;
	public static final int ROUTE_NOT_FOUND = 0x25;

	public static final int NO_DISCOVERY_OVERHEAD = 0x00;
	public static final int ROUTE_DISCOVERY = 0x02;

	private final int retryCount;
	private final int deliveryStatus;
	private final int discoveryStatus;

	public XBeeDigiStatusResponse(int nodeID, int retryCount, int deliveryStatus, int discoveryStatus) {
		super(nodeID, XBeeFrame.TRANSMIT_STATUS_DIGI);
		this.retryCount = retryCount;
		this.deliveryStatus = deliveryStatus;
		this.discoveryStatus = discoveryStatus;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public int getDeliveryStatus() {
		return deliveryStatus;
	}

	public int getDiscoveryStatus() {
		return discoveryStatus;
	}

	public boolean isSuccess() {
		return deliveryStatus == XBeeDigiStatusResponse.SUCCESS;
	}

}
