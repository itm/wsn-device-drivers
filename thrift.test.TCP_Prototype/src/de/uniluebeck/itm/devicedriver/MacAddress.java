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

package de.uniluebeck.itm.devicedriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.util.StringUtils;

/**
 * <code>MacAddress</code> object representation.
 * 
 * @author Malte Legenhausen
 */
public class MacAddress {

	/** 
	 * Logger for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(MacAddress.class);

	/**
	 * Suppose the MAC address is: 00:15:8D:00:00:04:7D:50. Then 0x00 will be
	 * stored at address[0] and 0x50 at address[7]. The least significant value
	 * isx50. 0x00 0x15 0x8D 0x00 0x00 0x04 0x7D 0x50 
	 */
	private byte[] address = {};

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
	public MacAddress(int lower16) {
		address = new byte[8];
		for (int i = 0; i < address.length; ++i) {
			address[i] = 0;
		}
		
		address[6] = (byte) (lower16 >> 8 & 0xFF);
		address[7] = (byte) (lower16 & 0xFF);
	}

	/**
	 * Constructor.
	 * 
	 * @param address Address as byte array.
	 */
	public MacAddress(byte[] address) {
		setMacBytes(address);
	}

	/**
	 * Constructor.
	 * 
	 * @param address The address as byte array.
	 * @param offset The mac address offset.
	 */
	public MacAddress(byte[] address, int offset) {
		byte mac[] = new byte[8];
		System.arraycopy(address, offset, mac, 0, mac.length);
		setMacBytes(mac);
	}

	/**
	 * Returns the MAC address as string in hex format
	 */
	public String getMacString() {
		return StringUtils.toHexString(getMacBytes());
	}

	/**
	 * Returns the mac address as byte array.
	 * 
	 * @return The mac address as byte array.
	 */
	public byte[] getMacBytes() {
		byte[] tmp = new byte[address.length];
		System.arraycopy(address, 0, tmp, 0, address.length);
		return tmp;
	}

	/**
	 * Setter for the mac address.
	 * 
	 * @param address The mac address as byte array.
	 */
	public void setMacBytes(byte[] address) {
		this.address = new byte[8];
		System.arraycopy(address, 0, this.address, 0, address.length);
		if (address.length > 8) {
			logger.warn("Supplied address is longer than 8 byte. Trimmed to 8.");
		}
	}

	/**
	 * Getter for the last two bytes of the mac address.
	 * 
	 * @return Returns the last two bytes of the mac address.
	 */
	public int getMacLowest16() {
		byte[] address = getMacBytes();
		return address[6] * 256 + address[7];
	}

	/**
	 * Checks if the lowest 16 bit of the address equals to the given integer id.
	 * 
	 * @return True if equal else false.
	 */
	public boolean equalsLower16(int id) {
		return id == getMacLowest16();
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof MacAddress) {
			MacAddress m = (MacAddress) o;
			m.getMacString().equals(getMacString());
		}
		return super.equals(o);
	}
	
	@Override
	public String toString() {
		return getMacString();
	}
}