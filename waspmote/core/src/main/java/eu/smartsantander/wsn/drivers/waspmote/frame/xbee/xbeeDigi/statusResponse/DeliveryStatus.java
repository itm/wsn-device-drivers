package eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.statusResponse;

public enum DeliveryStatus {
	SUCCESS(0x00, "Success"),
	MAC_ACK_FAILURE(0x01, "MAC ACK Failure"),
	INVALID_DESTINATION_ENDPOINT(0x15, "Invalid Destination Endpoint"),
	NETWORK_ACK_FAILURE(0x21, "Network ACK Failure"),
	ROUTE_NOT_FOUND(0x25, "Route Not Found");

	private final int value;
	private final String meaning;

	private DeliveryStatus(int value, String meaning) {
		this.value = value;
		this.meaning = meaning;
	}

	public static DeliveryStatus getDeliveryStatus(byte value) {
		switch (value) {
		case (byte) 0x00:
			return SUCCESS;
		case (byte) 0x01:
			return MAC_ACK_FAILURE;
		case (byte) 0x15:
			return INVALID_DESTINATION_ENDPOINT;
		case (byte) 0x21:
			return NETWORK_ACK_FAILURE;
		case (byte) 0x25:
			return ROUTE_NOT_FOUND;
		default:
			return null;
		}
	}

	public byte value() {
		return ((byte) (value & 0x00ff));
	}

	@Override
	public String toString() {
		return meaning;
	}
}
