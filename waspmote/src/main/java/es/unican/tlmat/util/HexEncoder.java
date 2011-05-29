package es.unican.tlmat.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author TLMAT UC
 */
public class HexEncoder {
	private final byte[] encodingTable = {
			(byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
			(byte) '8', (byte) '9', (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F'
			};

	private final byte[] prefix_0x = { (byte) '0', (byte) 'x' };

	private final byte[] decodingTable = new byte[128];

	private void initialiseDecodingTable() {
		for (int i = 0; i < encodingTable.length; i++) {
			decodingTable[encodingTable[i]] = (byte) i;
		}

		decodingTable['a'] = decodingTable['A'];
		decodingTable['b'] = decodingTable['B'];
		decodingTable['c'] = decodingTable['C'];
		decodingTable['d'] = decodingTable['D'];
		decodingTable['e'] = decodingTable['E'];
		decodingTable['f'] = decodingTable['F'];
	}

	protected HexEncoder() {
		initialiseDecodingTable();
	}

	/**
	 * Encode the input byte array data producing an Hex output stream.
	 *
	 * @return the number of bytes produced.
	 */
	protected int encode(byte[] data, int offset, int length, Character separator, boolean add_0x, OutputStream out)
			throws IOException {
		for (int i = offset; i < (offset + length); i++) {
			int v = data[i] & 0x00ff;

			if (add_0x) {
				out.write(prefix_0x);
			}

			out.write(encodingTable[(v >>> 4)]);
			out.write(encodingTable[v & 0x000f]);

			if ((separator != null) && (i != (offset + length - 1))) {
				out.write(separator);
			}
		}
		return length * 2 + (add_0x ? length * 2 : 0) + ((separator == null) ? 0 : length - 1);
	}

	private boolean isIgnoredChar(char c) {
		return (c == ' ' || c == ':' || c == '\n' || c == '\r' || c == '\t');
	}

	/**
	 * Decode the Hex encoded String data writing it to the given output stream,
	 * characters included in isIgnoredChar method will be ignored.
	 *
	 * @return the number of bytes produced.
	 */
	protected int decode(String data, OutputStream out) throws IOException {
		byte b1, b2;
		int length = 0;

		int end = data.length();
		while (end > 0) {
			if (!isIgnoredChar(data.charAt(end - 1))) {
				break;
			}
			end--;
		}

		int i = 0;
		while (i < end) {
			while (i < end && isIgnoredChar(data.charAt(i))) {
				i++;
			}
			while (i < end - 1 && data.charAt(i) == '0' & data.charAt(i + 1) == 'x') {
				i++;
			}
			b1 = decodingTable[data.charAt(i++)];
			while (i < end && isIgnoredChar(data.charAt(i))) {
				i++;
			}
			b2 = decodingTable[data.charAt(i++)];
			out.write((b1 << 4) | b2);
			length++;
		}
		return length;
	}
}
