package eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbee802154.statusResponse;

public enum Status {
	SUCCESS(0x00, "Success"),
	NO_ACK_RECEIVED(0x01, "All retries are expired and no ACK is received"),
	CCA_FAILURE(0x02, "CCA Failure"),
	PURGED(0x03, "Purged (coordinator timed out of an indirect transmission)");

	private final int value;
	private final String meaning;

	private Status(int value, String meaning) {
		this.value = value;
		this.meaning = meaning;
	}

	public static Status getStatus(int value) {
		switch (value) {
		case (byte) 0x00:
			return SUCCESS;
		case (byte) 0x01:
			return NO_ACK_RECEIVED;
		case (byte) 0x02:
			return CCA_FAILURE;
		case (byte) 0x03:
			return PURGED;
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
