package es.unican.tlmat.util;

import de.uniluebeck.itm.wsn.drivers.core.MacAddress;

/**
 * @author TLMAT UC
 */
public class ExtendedMacAddress extends MacAddress {

	/**
	 * @param address
	 * @param offset
	 */
	public ExtendedMacAddress(byte[] address, int offset) {
		super(address, offset);
	}

	/**
	 * @param address
	 */
	public ExtendedMacAddress(byte[] address) {
		super(address);
	}

	public ExtendedMacAddress(String hexString) {
		this(HexUtils.hexString2ByteArray(hexString));
	}

	@Override
	public String toString() {
		return HexUtils.byteArray2HexString(getMacBytes(), "");
	}




}
