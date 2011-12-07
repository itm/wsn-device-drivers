package eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.statusResponse;

public enum DiscoveryStatus {
	NO_DISCOVERY_OVERHEAD(0x00, "No Discovery Overhead"),
	ROUTE_DISCOVERY(0x02, "Route Discovery");

	private final int value;
	private final String meaning;

	private DiscoveryStatus(int value, String meaning) {
		this.value = value;
		this.meaning = meaning;
	}

	public static DiscoveryStatus getDiscoveryStatus(int value) {
		switch (value) {
		case (byte) 0x00:
			return NO_DISCOVERY_OVERHEAD;
		case (byte) 0x02:
			return ROUTE_DISCOVERY;
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
