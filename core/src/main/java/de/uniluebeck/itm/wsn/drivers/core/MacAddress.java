/**********************************************************************************************************************
 * Copyright (c) 2010, coalesenses GmbH                                                                               *
 * All rights reserved.                                                                                               *
 *                                                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the   *
 * following conditions are met:                                                                                      *
 *                                                                                                                    *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following *
 *   disclaimer.                                                                                                      *
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the        *
 *   following disclaimer in the documentation and/or other materials provided with the distribution.                 *
 * - Neither the name of the coalesenses GmbH nor the names of its contributors may be used to endorse or promote     *
 *   products derived from this software without specific prior written permission.                                   *
 *                                                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, *
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE      *
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,         *
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE *
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF    *
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY   *
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                                *
 **********************************************************************************************************************/

package de.uniluebeck.itm.wsn.drivers.core;

/**
 * <code>MacAddress</code> object representation.
 *
 * @author Malte Legenhausen
 */
public class MacAddress {

	/**
	 * The length of a mac address.
	 */
	private static final int LENGTH = 8;

	/**
	 * Suppose the MAC address is: 00:15:8D:00:00:04:7D:50. Then 0x00 will be stored at address[0] and 0x50 at address[7].
	 * The least significant value isx50. 0x00 0x15 0x8D 0x00 0x00 0x04 0x7D 0x50
	 */
	private final byte[] array = new byte[LENGTH];

	/**
	 * Constructor.
	 *
	 * @param macAddress Address as long value.
	 */
    public MacAddress(final long macAddress) {
		setArray(macAddress);
	}

	/**
	 * Constructor.
	 *
	 * @param macAddress Address as byte array.
	 */
	public MacAddress(final byte[] macAddress) {
		System.arraycopy(macAddress, 0, array, 0, LENGTH);
	}

	/**
	 * Constructs an instance from a String value. The value may either be specified as decimal ("1234"),
	 * hexadecimal ("0x4d2") or binary ("0b10011010010").
	 *
	 * @param macAddress Address as String value
	 */
	public MacAddress(final String macAddress) {
		if (macAddress.startsWith("0x")) {
			setArray(Long.parseLong(macAddress.substring(2), 16));
		} else if (macAddress.startsWith("0b")) {
			setArray(Long.parseLong(macAddress.substring(2), 2));
		} else {
			setArray(Long.parseLong(macAddress, 10));
		}
	}

	/**
	 * Returns the mac address as byte array.
	 *
	 * @return The mac address as byte array.
	 */
	public byte[] toByteArray() {
		final byte[] tmp = new byte[LENGTH];
		System.arraycopy(array, 0, tmp, 0, LENGTH);
		return tmp;
	}

	/**
	 * Returns the MAC address as long value.
	 *
	 * @return the MAC address as long value
	 */
	public long toLong() {
		return  ((long) array[0] & 0xff) << 56 |
                ((long) array[1] & 0xff) << 48 |
                ((long) array[2] & 0xff) << 40 |
                ((long) array[3] & 0xff) << 32 |
                ((long) array[4] & 0xff) << 24 |
                ((long) array[5] & 0xff) << 16 |
                ((long) array[6] & 0xff) <<  8 |
                ((long) array[7] & 0xff) <<  0;
	}

	@Override
	public String toString() {
		return toHexString();
	}

	public String toHexString() {
		return "0x" + Long.toString(toLong(), 16);
	}

	public String toDecString() {
		return Long.toString(toLong(), 10);
	}

	public String toBinString() {
		return "0b" + Long.toString(toLong(), 2);
	}

	private void setArray(final long value) {
		array[0] = (byte) (value >>> 56);
        array[1] = (byte) (value >>> 48);
        array[2] = (byte) (value >>> 40);
        array[3] = (byte) (value >>> 32);
        array[4] = (byte) (value >>> 24);
        array[5] = (byte) (value >>> 16);
        array[6] = (byte) (value >>> 8);
        array[7] = (byte) (value >>> 0);
	}
}