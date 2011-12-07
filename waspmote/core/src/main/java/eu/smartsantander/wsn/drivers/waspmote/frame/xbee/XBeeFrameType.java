package eu.smartsantander.wsn.drivers.waspmote.frame.xbee;

public enum XBeeFrameType {
	TRANSMIT_REQUEST_DIGIMESH(0x10, XBeeProtocol.PROTOCOL_DIGIMESH),
	TRANSMIT_STATUS_DIGIMESH(0x8B, XBeeProtocol.PROTOCOL_DIGIMESH),
	RECEIVE_PACKET_DIGIMESH(0x90, XBeeProtocol.PROTOCOL_DIGIMESH),
//    AT_CMD_REQUEST_DIGIMESH(0x08, XBeeProtocol.PROTOCOL_DIGIMESH),
//    AT_CMD_RESPONSE_DIGIMESH(0x88, XBeeProtocol.PROTOCOL_DIGIMESH),
    REMOTE_AT_CMD_REQUEST_DIGIMESH(0x17, XBeeProtocol.PROTOCOL_DIGIMESH),
    REMOTE_AT_CMD_RESPONSE_DIGIMESH(0x97, XBeeProtocol.PROTOCOL_DIGIMESH),

	TRANSMIT_REQUEST_802154(0x00, XBeeProtocol.PROTOCOL_802154),
	TRANSMIT_STATUS_802154(0x89, XBeeProtocol.PROTOCOL_802154),
	RECEIVE_PACKET_802154(0x80, XBeeProtocol.PROTOCOL_802154);
//    AT_CMD_REQUEST_802154(0x07, XBeeProtocol.PROTOCOL_802154),
//    AT_CMD_RESPONSE_802154(0x87, XBeeProtocol.PROTOCOL_802154),
//    REMOTE_AT_CMD_REQUEST_802154(0x16, XBeeProtocol.PROTOCOL_802154),
//    REMOTE_AT_CMD_RESPONSE_802154(0x96, XBeeProtocol.PROTOCOL_802154);

	public enum XBeeProtocol { PROTOCOL_DIGIMESH, PROTOCOL_802154, PROTOCOL_UNKNOWN; }

	private final int value;
	private final XBeeProtocol protocol;

	private XBeeFrameType(int value, XBeeProtocol protocol) {
		this.value = value;
		this.protocol = protocol;
	}

	public static XBeeFrameType getXbeeFrameType(byte value) {
		switch (value) {
		case (byte) 0x10:
			return TRANSMIT_REQUEST_DIGIMESH;
		case (byte) 0x8B:
			return TRANSMIT_STATUS_DIGIMESH;
		case (byte) 0x90:
			return RECEIVE_PACKET_DIGIMESH;
//		case (byte) 0x08:
//			return AT_CMD_REQUEST_DIGIMESH;
//		case (byte) 0x88:
//			return AT_CMD_RESPONSE_DIGIMESH;
		case (byte) 0x17:
			return REMOTE_AT_CMD_REQUEST_DIGIMESH;
		case (byte) 0x97:
			return REMOTE_AT_CMD_RESPONSE_DIGIMESH;
		case (byte) 0x00:
			return TRANSMIT_REQUEST_802154;
		case (byte) 0x89:
			return TRANSMIT_STATUS_802154;
		case (byte) 0x80:
			return RECEIVE_PACKET_802154;
//        case (byte) 0x07:
//			return AT_CMD_REQUEST_802154;
//		case (byte) 0x87:
//			return AT_CMD_RESPONSE_802154;
//		case (byte) 0x16:
//			return REMOTE_AT_CMD_REQUEST_802154;
//		case (byte) 0x96:
//			return REMOTE_AT_CMD_RESPONSE_802154;
		default:
			return null;
		}
	}

	public byte value() {
		return ((byte) (value & 0x00ff));
	}

	public XBeeProtocol protocol() {
		return protocol;
	}

}
