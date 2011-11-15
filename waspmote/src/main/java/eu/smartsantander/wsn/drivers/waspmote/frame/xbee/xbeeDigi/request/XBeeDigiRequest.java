package eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.request;

import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeAbstractRequest;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrameType;

/**
 * @author TLMAT UC
 */
public class XBeeDigiRequest extends XBeeAbstractRequest {

	public static final int DEFAULT_BROADCAST_RADIUS = 0x00;

	public static final int DISABLE_ACK = 0x01;
	public static final int NO_ROUTE_DISCOVERY = 0x02;

	private final int broadcastRadius;
	private final int transmitOptions;

	public XBeeDigiRequest(int nodeID, int broadcastRadius, int transmitOptions, byte[] payload) {
		super(nodeID, XBeeFrameType.TRANSMIT_REQUEST_DIGIMESH, payload);
		this.broadcastRadius = broadcastRadius;
		this.transmitOptions = 0x00 | transmitOptions;
	}

	public XBeeDigiRequest(int nodeID, byte[] payload) {
		this(nodeID, XBeeDigiRequest.DEFAULT_BROADCAST_RADIUS, 0x00, payload);
	}

	public int getBroadcastRadius() {
		return broadcastRadius;
	}

	public int getTransmitOptions() {
		return transmitOptions;
	}

}
