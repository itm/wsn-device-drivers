package es.unican.tlmat.wsn.drivers.waspmote.frame.xbeeDigi;

import es.unican.tlmat.wsn.drivers.waspmote.frame.XBeeFrame;

/**
 * @author TLMAT UC
 */
public class XBeeDigiRequest extends XBeeFrame {

	public static final int DEFAULT_BROADCAST_RADIUS = 0x00;

	public static final int DISABLE_ACK = 0x01;
	public static final int NO_ROUTE_DISCOVERY = 0x02;

	private final int broadcastRadius;
	private final int transmitOptions;
	private final byte[] payload;

	public XBeeDigiRequest(int nodeID, int broadcastRadius, int transmitOptions, byte[] payload) {
		super(nodeID, XBeeFrame.TRANSMIT_REQUEST);
		this.broadcastRadius = broadcastRadius;
		this.transmitOptions = 0x00 | transmitOptions;
		this.payload = payload;
	}

	public XBeeDigiRequest(int nodeID, byte[] payload) {
		this(nodeID, XBeeDigiRequest.DEFAULT_BROADCAST_RADIUS, 0, payload);
	}

	public int getBroadcastRadius() {
		return broadcastRadius;
	}

	public int getTransmitOptions() {
		return transmitOptions;
	}

	public byte[] getPayload() {
		return payload;
	}

}
