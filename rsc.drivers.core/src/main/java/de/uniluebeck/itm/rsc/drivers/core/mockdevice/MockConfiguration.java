package de.uniluebeck.itm.rsc.drivers.core.mockdevice;

import de.uniluebeck.itm.rsc.drivers.core.ChipType;
import de.uniluebeck.itm.rsc.drivers.core.MacAddress;


/**
 * A configuration pojo for the <code>MockDevice</code>.
 * 
 * @author Malte Legenhausen
 */
public class MockConfiguration {

	/**
	 * Default channels for this configuration.
	 */
	private static final int[] DEFAULT_CHANNELS = new int[] {11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
	
	/**
	 * The length of the <code>MockDevice</code> flash.
	 */
	private static final int LENGTH = 1024;
	
	/**
	 * The channels for this device.
	 */
	private int[] channels = DEFAULT_CHANNELS;
	
	/**
	 * The <code>MacAddress</code> for the <code>MockDevice</code>.
	 */
	private MacAddress macAddress;
	
	/**
	 * The <code>ChipType</code> for the <code>MockDevice</code>.
	 */
	private ChipType chipType;
	
	/**
	 * The flash of the <code>MockDevice</code>.
	 */
	private byte[] flashRom;

	/**
	 * Constructor.
	 */
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
