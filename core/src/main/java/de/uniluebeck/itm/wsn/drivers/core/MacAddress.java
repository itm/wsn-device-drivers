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

import java.util.Arrays;

/**
 * <code>MacAddress</code> object representation.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class MacAddress {

	/**
	 * The length of a mac address.
	 */
	private static final int LENGTH = 8;

	private static final int FULL_BYTE_MASK = 0xff;

	private static final int FULL_BYTE_SHIFT = 8;

	private static final int DEC_BASE = 10;

	private static final int HEX_BASE = 16;

	/**
	 * Suppose the MAC address is: 00:15:8D:00:00:04:7D:50. Then 0x00 will be stored at address[0] 
	 * and 0x50 at address[7].
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
	 * Constructs an instance from a String value. The value may either be specified as decimal ("1234"), hexadecimal
	 * ("0x4d2") or binary ("0b10011010010").
	 *
	 * @param macAddress Address as String value
	 */
	public MacAddress(final String macAddress) {
		if (macAddress.startsWith("0x")) {
			setArray(Long.parseLong(macAddress.substring(2), HEX_BASE));
		} else if (macAddress.startsWith("0b")) {
			setArray(Long.parseLong(macAddress.substring(2), 2));
		} else {
			setArray(Long.parseLong(macAddress, DEC_BASE));
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
		long result = 0L;
		for (int i = 0; i < array.length; i++) {
			result |= (array[LENGTH - 1 - i] & FULL_BYTE_MASK) << (i * FULL_BYTE_SHIFT);
		}
		return result;
	}

	@Override
	public String toString() {
		return toHexString();
	}

	public String toHexString() {
		return "0x" + Long.toString(toLong(), HEX_BASE);
	}

	public String toDecString() {
		return Long.toString(toLong(), DEC_BASE);
	}

	public String toBinString() {
		return "0b" + Long.toString(toLong(), 2);
	}

	public MacAddress to16BitMacAddress() {
		byte[] result = new byte[LENGTH];
		int offset = LENGTH - 2;
		System.arraycopy(array, offset, result, offset, 2);
		return new MacAddress(result);
	}

	private void setArray(final long value) {
		for (int i = 0; i < array.length; i++) {
			array[LENGTH - 1 - i] = (byte) (value >>> (FULL_BYTE_SHIFT * i));
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final MacAddress that = (MacAddress) o;

		return Arrays.equals(array, that.array);

	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(array);
	}
}
