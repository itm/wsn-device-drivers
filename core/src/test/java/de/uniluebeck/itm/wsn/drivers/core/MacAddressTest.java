package de.uniluebeck.itm.wsn.drivers.core;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

public class MacAddressTest {

    private static final String MAC_HEX_STRING = "00:15:8D:00:00:04:7D:50";
    private static final String MAC_HEX_STRING2 = "00158D0000047D50";
    private static final String MAC_HEX_STRING3 = "0x00 0x15 0x8D 0x00 0x00 0x04 0x7D 0x50";

    private static final long MAC_LONG = 6066005650734416L;

    private static final byte[] MAC_BYTES = new byte[]{
            (byte) 0x00, (byte) 0x15, (byte) 0x8D, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x7D, (byte) 0x50};

    private static final byte[] MAC_BYTES_16_BIT = new byte[]{0, 0, 0, 0, 0, 0, (byte) 0x7D, (byte) 0x50};

    private static final byte[] MAC_BYTES_OTHER = new byte[]{0, 1, 0, 0, 0, 1, 35, 70};

    @Test
    public void fromStringToString() {
        assertEquals(MAC_HEX_STRING, new MacAddress(MAC_HEX_STRING).toString());
        assertEquals(MAC_HEX_STRING, new MacAddress(MAC_HEX_STRING2).toString());
        assertEquals(MAC_HEX_STRING, new MacAddress(MAC_HEX_STRING3).toString());
    }

    @Test
    public void fromStringToLong() {
        assertEquals(MAC_LONG, new MacAddress(MAC_HEX_STRING).toLong());
    }

    @Test
    public void fromStringToByteArray() {
        assertArrayEquals(MAC_BYTES, new MacAddress(MAC_HEX_STRING).toByteArray());
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
        assertEquals(MAC_HEX_STRING, new MacAddress(MAC_LONG).toString());
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
        assertEquals(MAC_HEX_STRING, new MacAddress(MAC_BYTES).toString());
    }

    @Test
    public void equalsTest() {

        assertTrue(new MacAddress(MAC_BYTES).equals(new MacAddress(MAC_BYTES)));
        assertTrue(new MacAddress(MAC_HEX_STRING).equals(new MacAddress(MAC_BYTES)));
        assertTrue(new MacAddress(MAC_HEX_STRING).equals(new MacAddress(MAC_HEX_STRING2)));
        assertTrue(new MacAddress(MAC_HEX_STRING2).equals(new MacAddress(MAC_HEX_STRING3)));

        assertFalse(new MacAddress(MAC_BYTES).equals(new MacAddress(MAC_BYTES_OTHER)));
        assertFalse(new MacAddress(MAC_HEX_STRING).equals(new MacAddress(MAC_BYTES_OTHER)));
        assertFalse(new MacAddress(MAC_LONG).equals(new MacAddress(MAC_BYTES_OTHER)));
    }

    @Test
    public void hashCodeTest() {

        assertEquals(new MacAddress(MAC_BYTES).hashCode(), new MacAddress(MAC_BYTES).hashCode());
        assertEquals(new MacAddress(MAC_HEX_STRING).hashCode(), new MacAddress(MAC_BYTES).hashCode());

        assertThat(new MacAddress(MAC_BYTES).hashCode(), not(equalTo(new MacAddress(MAC_BYTES_OTHER).hashCode())));
        assertThat(new MacAddress(MAC_HEX_STRING).hashCode(), not(equalTo(new MacAddress(MAC_BYTES_OTHER).hashCode())));
    }

    @Test
    public void to16BitMacAddressTest() {

        assertEquals(new MacAddress(MAC_BYTES_16_BIT), new MacAddress(MAC_BYTES).to16BitMacAddress());
        assertEquals(new MacAddress(MAC_BYTES_16_BIT), new MacAddress(MAC_HEX_STRING).to16BitMacAddress());
    }

}
