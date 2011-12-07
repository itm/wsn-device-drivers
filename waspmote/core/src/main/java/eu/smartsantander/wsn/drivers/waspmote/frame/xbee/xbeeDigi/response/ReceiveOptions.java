package eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.response;

public enum ReceiveOptions {
	PACKET_ACKNOWLEDGED(0x01, "Packet Acknowledged"),
	BROADCAST_PACKET(0x02, "Packet was a broadcast packet");

	private final int value;
	private final String meaning;

	private ReceiveOptions(int value, String meaning) {
		this.value = value;
		this.meaning = meaning;
	}

	public static ReceiveOptions getReceiveOptions(byte value) {
		switch (value) {
		case (byte) 0x01:
			return PACKET_ACKNOWLEDGED;
		case (byte) 0x02:
			return BROADCAST_PACKET;
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
