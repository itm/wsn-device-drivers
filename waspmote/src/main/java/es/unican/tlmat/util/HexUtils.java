package es.unican.tlmat.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author TLMAT UC
 */
public class HexUtils {

	private static final HexEncoder encoder = new HexEncoder();

	/**
	 * Encode the full input byte array data, producing an Hex String using ' '
	 * as byte separator and without using 0x prefix.
	 */
	public static String byteArray2HexString(byte[] byteArray) {
		return byteArray2HexString(byteArray, 0, byteArray.length, ' ', false);
	}

	/**
	 * Encode the full input byte array data, producing an Hex String using sep
	 * parameter as byte separator and without using 0x prefix.
	 */
	public static String byteArray2HexString(byte[] byteArray, Character sep) {
		return byteArray2HexString(byteArray, 0, byteArray.length, sep, false);
	}

	/**
	 * Encode the full input byte array data, producing an Hex String using sep
	 * parameter as byte separator and deciding if 0x prefix has to be used by
	 * add_0x parameter.
	 */
	public static String byteArray2HexString(byte[] byteArray, Character sep, boolean add_0x) {
		return byteArray2HexString(byteArray, 0, byteArray.length, sep, add_0x);
	}

	/**
	 * Encode the sub-array of input byte array defined by offset and length
	 * parameters, producing an Hex String using sep parameter as byte separator
	 * and deciding if 0x prefix has to be used by add_0x parameter.
	 */
	public static String byteArray2HexString(byte[] byteArray, int offset, int length, Character sep, boolean add_0x) {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try {
			encoder.encode(byteArray, offset, length, sep, add_0x, bOut);
		} catch (IOException e) {
			throw new RuntimeException("Exception encoding Hex string: " + e);
		}
		return bOut.toString();
	}

	/**
	 * Decode the Hex encoded String data into a byte array. Characters included
	 * in encoder.isIgnoredChar() method will be ignored.
	 */
	public static byte[] hexString2ByteArray(String hexString) {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		try {
			encoder.decode(hexString, bOut);
		} catch (IOException e) {
			throw new RuntimeException("Exception decoding Hex string: " + e);
		}
		return bOut.toByteArray();
	}

	// public static String byteArray2HexString(byte[] byteArray, String sep) {
	// StringBuffer hexString = new StringBuffer();
	// for (int i = 0; i < byteArray.length; i++) {
	// if (i == byteArray.length - 1) {
	// hexString.append(String.format("%02X", byteArray[i]));
	// } else {
	// hexString.append(String.format("%02X" + sep, byteArray[i]));
	// }
	// }
	// return hexString.toString();
	// }
}
