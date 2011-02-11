package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.MacAddress;

public class MockConfiguration {

	private static final int[] DEFAULT_CHANNELS = new int[] {11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
	
	private static final int LENGTH = 1024;
	
	private int[] channels = DEFAULT_CHANNELS;
	
	private MacAddress macAddress;
	
	private ChipType chipType;
	
	private byte[] flashRom;


	public MockConfiguration() {
		flashRom = new byte[LENGTH];
		chipType = ChipType.UNKNOWN;
		macAddress = new MacAddress(0);
	}
	
	public MacAddress getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(final MacAddress macAddress) {
		this.macAddress = macAddress;
	}

	public ChipType getChipType() {
		return chipType;
	}

	public void setChipType(final ChipType chipType) {
		this.chipType = chipType;
	}

	public byte[] getFlashRom() {
		return flashRom;
	}

	public void setFlashRom(final byte[] flashRom) {
		this.flashRom = flashRom;
	}

	public int[] getChannels() {
		return channels;
	}

	public void setChannels(final int[] channels) {
		this.channels = channels;
	}
}
