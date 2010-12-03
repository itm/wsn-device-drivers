package de.uniluebeck.itm.devicedriver.mockdevice;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.MacAddress;

public class MockConfiguration {

	private MacAddress macAddress;
	
	private ChipType chipType;
	
	private byte[] flashRom;
	
	private int[] channels = new int[] {11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};

	public MockConfiguration() {
		flashRom = new byte[1024];
		chipType = ChipType.UNKNOWN;
		macAddress = new MacAddress(0);
	}
	
	public MacAddress getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(MacAddress macAddress) {
		this.macAddress = macAddress;
	}

	public ChipType getChipType() {
		return chipType;
	}

	public void setChipType(ChipType chipType) {
		this.chipType = chipType;
	}

	public byte[] getFlashRom() {
		return flashRom;
	}

	public void setFlashRom(byte[] flashRom) {
		this.flashRom = flashRom;
	}

	public int[] getChannels() {
		return channels;
	}

	public void setChannels(int[] channels) {
		this.channels = channels;
	}
}
