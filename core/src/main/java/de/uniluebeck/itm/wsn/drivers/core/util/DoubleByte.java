package de.uniluebeck.itm.wsn.drivers.core.util;

import com.google.common.base.Preconditions;

/**
 * @author TLMAT UC
 */
public class DoubleByte {
	private final byte msb;
	private final byte lsb;
	private final int value;

	public DoubleByte(int value) {
		Preconditions.checkArgument(value <= 0xFFFF && value >= 0, "Value is out of range");

		this.value = value;
		this.msb = (byte) ((value >> 8) & 0x00ff);
		this.lsb = (byte) (value & 0x00ff);
	}

	public DoubleByte(byte msb, byte lsb) {
		this.msb = msb;
		this.lsb = lsb;
		this.value = ((msb & 0x00ff) << 8) | (lsb & 0x00ff);
	}

	public byte getMsb() {
		return msb;
	}

	public byte getLsb() {
		return lsb;
	}

	public int get16BitValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DoubleByte temp = (DoubleByte) o;
		if (lsb != temp.lsb) {
			return false;
		}
		if (msb != temp.msb) {
			return false;
		}
		return true;
	}

}
