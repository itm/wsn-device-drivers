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

import de.uniluebeck.itm.tr.util.StringUtils;

/**
 * <code>MacAddress</code> object representation.
 * 
 * @author Malte Legenhausen
 */
public class MacAddress {
	
	/**
	 * Bit mask for the mac address entries.
	 */
	private static final int BIT_MASK = 0xFF;
	
	/**
	 * Shift for the penultimate byte fo output as int.
	 */
	private static final int PENULTIMATE_SHIFT = 8;
	
	/**
	 * The length of a mac address.
	 */
	private static final int LENGTH = 8;
	
	/**
	 * Index of the last element of the address.
	 */
	private static final int ULTIMATE = 7;
	
	/**
	 * Index of the next to last element of the address.
	 */
	private static final int PENULTIMATE = 6;

	/**
	 * Suppose the MAC address is: 00:15:8D:00:00:04:7D:50. Then 0x00 will be
	 * stored at address[0] and 0x50 at address[7]. The least significant value
	 * isx50. 0x00 0x15 0x8D 0x00 0x00 0x04 0x7D 0x50 
	 */
	private final byte[] address = new byte[LENGTH];

	/**
	 * Constructor.
	 */
	public MacAddress() {
		
	}
	
	/**
	 * Constructor.
	 * 
	 * @param lower16 The last two bytes of the mac address.
	 */
	public MacAddress(final int lower16) {
		address[PENULTIMATE] = (byte) (lower16 >> PENULTIMATE_SHIFT & BIT_MASK);
		address[ULTIMATE] = (byte) (lower16 & BIT_MASK);
	}

	/**
	 * Constructor.
	 * 
	 * @param address Address as byte array.
	 */
	public MacAddress(final byte[] address) {
		this(address, 0);
	}

	/**
	 * Constructor.
	 * 
	 * @param address The address as byte array.
	 * @param offset The mac address offset.
	 */
	public MacAddress(final byte[] address, final int offset) {
		System.arraycopy(address, offset, this.address, 0, LENGTH);
	}

	/**
	 * Returns the mac address as byte array.
	 * 
	 * @return The mac address as byte array.
	 */
	public byte[] getMacBytes() {
		final byte[] tmp = new byte[LENGTH];
		System.arraycopy(address, 0, tmp, 0, LENGTH);
		return tmp;
	}

	/**
	 * Getter for the last two bytes of the mac address.
	 * 
	 * @return Returns the last two bytes of the mac address.
	 */
	public int getMacLowest16() {
		return address[PENULTIMATE] >> PENULTIMATE_SHIFT + address[ULTIMATE];
	}

	/**
	 * Checks if the lowest 16 bit of the address equals to the given integer id.
	 * 
	 * @param id The low 2 bytes.
	 * @return True if equal else false.
	 */
	public boolean equalsLower16(final int id) {
		return id == getMacLowest16();
	}

	@Override
	public boolean equals(final Object o) {
		if (o != null && o instanceof MacAddress) {
			final MacAddress m = (MacAddress) o;
			m.toString().equals(toString());
		}
		return super.equals(o);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public String toString() {
		return StringUtils.toHexString(address);
	}
}