package es.unican.tlmat.util;

import org.bouncycastle.util.encoders.Hex;

/**
 * @author TLMAT UC
 */
public class HexUtils {

	public static String byteArray2HexString(byte[] byteArray) {
		return byteArray2HexString(byteArray, " ");
	}

	public static String byteArray2HexString(byte[] byteArray, String sep) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (i == byteArray.length - 1) {
				hexString.append(String.format("%02X", byteArray[i]));
			} else {
				hexString.append(String.format("%02X" + sep, byteArray[i]));
			}
		}
		return hexString.toString();
	}

	public static byte[] hexString2ByteArray(String hexString) {
		return Hex.decode(hexString);
	}
}
