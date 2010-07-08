package de.uniluebeck.itm.devicedriver.util;

/**
 * Utility class for String operations.
 * 
 * @author Malte Legenhausen
 */
public class StringUtils {

	/**
	 * Convert the given bytes to a <code>String</code>.
	 * 
	 * @param tmp The bytes that has to be converted.
	 * @return The bytes in <code>String</code> representation.
	 */
	public static String toHexString(final byte[] tmp) {
		return toHexString(tmp, 0, tmp.length);
	}

	/**
	 * Convert the given bytes to a <code>String</code>.
	 * 
	 * @param tmp The bytes that has to be converted.
	 * @param offset Offset from with the convertion has to be begun.
	 * @param length The amount of bytes that has to be converted.
	 * @return The bytes in <code>String</code> representation.
	 */
	public static String toHexString(final byte[] tmp, final int offset, final int length) {
		StringBuffer s = new StringBuffer();
		for (int i = offset; i < offset + length; ++i) {
			if (s.length() > 0)
				s.append(' ');
			s.append("0x");
			s.append(Integer.toHexString(tmp[i] & 0xFF));
		}
		return s.toString();
	}

	/**
	 * Convertes the given bytes to the appropriate ASCII character.
	 * 
	 * @param tmp The bytes that has to be converted.
	 * @return The bytes in ASCII <code>String</code> representation.
	 */
	public static String toASCIIString(final byte[] tmp) {
		StringBuffer sb = new StringBuffer("");

		for (byte b : tmp) {
			if (b == 0x0D)
				sb.append("<CR>");
			else if (b == 0x0A)
				sb.append("<LF>");
			else {
				char chr = (char) b;
				sb.append(chr);
			}
		}
		return sb.toString();
	}
}
