package eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.at;

/**
 * @author TLMAT UC
 */
public enum CommandStatus {
    OK(0x00, "OK"),
	ERROR(0x01, "ERROR"),
	INVALID_COMMAND(0x02, "Invalid command"),
	INVALID_PARAMETER(0x03, "Invalid Parameter");

    private final int value;
	private final String meaning;

	private CommandStatus(int value, String meaning) {
		this.value = value;
		this.meaning = meaning;
	}

	public static CommandStatus getCommandStatus(byte value) {
		switch (value) {
		case (byte) 0x00:
			return OK;
		case (byte) 0x01:
			return ERROR;
		case (byte) 0x02:
			return INVALID_COMMAND;
		case (byte) 0x03:
			return INVALID_PARAMETER;
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
