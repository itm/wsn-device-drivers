package de.uniluebeck.itm.wsn.drivers.core;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MacAddressTest {

	private static final String MAC_DEC_STRING = "1234";

	private static final String MAC_HEX_STRING = "0x4d2";

	private static final String MAC_BIN_STRING = "0b10011010010";

	private static final long MAC_LONG = 1234;

	private static final byte[] MAC_BYTES = new byte[] {0, 0, 0, 0, 0, 0, 4, -46};

	@Test
	public void fromStringToString() {
		assertEquals(MAC_DEC_STRING, new MacAddress(MAC_DEC_STRING).toDecString());
		assertEquals(MAC_DEC_STRING, new MacAddress(MAC_HEX_STRING).toDecString());
		assertEquals(MAC_DEC_STRING, new MacAddress(MAC_BIN_STRING).toDecString());

		assertEquals(MAC_HEX_STRING, new MacAddress(MAC_DEC_STRING).toHexString());
		assertEquals(MAC_HEX_STRING, new MacAddress(MAC_HEX_STRING).toHexString());
		assertEquals(MAC_HEX_STRING, new MacAddress(MAC_BIN_STRING).toHexString());

		assertEquals(MAC_BIN_STRING, new MacAddress(MAC_DEC_STRING).toBinString());
		assertEquals(MAC_BIN_STRING, new MacAddress(MAC_HEX_STRING).toBinString());
		assertEquals(MAC_BIN_STRING, new MacAddress(MAC_BIN_STRING).toBinString());
	}

	@Test
	public void fromStringToLong() {
		assertEquals(MAC_LONG, new MacAddress(MAC_DEC_STRING).toLong());
		assertEquals(MAC_LONG, new MacAddress(MAC_HEX_STRING).toLong());
		assertEquals(MAC_LONG, new MacAddress(MAC_BIN_STRING).toLong());
	}

	@Test
	public void fromStringToByteArray() {
		assertArrayEquals(MAC_BYTES, new MacAddress(MAC_DEC_STRING).toByteArray());
		assertArrayEquals(MAC_BYTES, new MacAddress(MAC_HEX_STRING).toByteArray());
		assertArrayEquals(MAC_BYTES, new MacAddress(MAC_BIN_STRING).toByteArray());
	}

	@Test
	public void fromLongToLong() {
		assertEquals(MAC_LONG, new MacAddress(MAC_LONG).toLong());
	}

	@Test
	public void fromLongToByteArray() {
		assertArrayEquals(MAC_BYTES, new MacAddress(MAC_LONG).toByteArray());
	}

	@Test
	public void fromLongToString() {
		assertEquals(MAC_DEC_STRING, new MacAddress(MAC_LONG).toDecString());
		assertEquals(MAC_HEX_STRING, new MacAddress(MAC_LONG).toHexString());
		assertEquals(MAC_BIN_STRING, new MacAddress(MAC_LONG).toBinString());
	}

	@Test
	public void fromByteArrayToByteArray() {
		assertArrayEquals(MAC_BYTES, new MacAddress(MAC_BYTES).toByteArray());
	}

	@Test
	public void fromByteArrayToLong() {
		assertEquals(MAC_LONG, new MacAddress(MAC_BYTES).toLong());
	}

	@Test
	public void fromByteArrayToString() {
		assertEquals(MAC_DEC_STRING, new MacAddress(MAC_BYTES).toDecString());
		assertEquals(MAC_HEX_STRING, new MacAddress(MAC_BYTES).toHexString());
		assertEquals(MAC_BIN_STRING, new MacAddress(MAC_BYTES).toBinString());
	}

}
