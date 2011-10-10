package de.uniluebeck.itm.wsn.drivers.test.isense; /**********************************************************************************************************************
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

import de.uniluebeck.itm.tr.util.StringUtils;

/**
 * A message packet returned by a isense device.
 * 
 * @author Malte Legenhausen
 */
public class MessagePacket {

	/** Special character */
	public static final byte STX = 0x02;

	/** Special character */
	public static final byte ETX = 0x03;

	/** Special character */
	public static final byte DLE = 0x10;

	/**
	 * 
	 */
	public static final byte CR = 0x0D;

	/**
	 * 
	 */
	public static final byte LF = 0x0A;

	/** */
	private static long nextIdCounter = 0;

	/**
	 * Byte mask for extracting the type.
	 */
	private static final int TYPE_MASK = 0xFF;

	/** */
	private byte[] content;

	/** */
	private int type = -1;

	/** */
	private long id = nextId();

	/**
	 * 
	 */
	protected MessagePacket() {
	}

	/**
	 * Constructor.
	 * 
	 * @param type The message type.
	 * @param content The content as byte array.
	 */
	public MessagePacket(final int type, final byte[] content) {
		this.type = type;
		this.content = new byte[content.length];
		System.arraycopy(content, 0, this.content, 0, content.length);
	}

	/**
	 * Generates the next unique id.
	 * 
	 * @return A unique long id.
	 */
	private synchronized static long nextId() {
		if (nextIdCounter >= Long.MAX_VALUE - 1)
			nextIdCounter = 0;

		return ++nextIdCounter;
	}

	/**
	 * Parse a given byte stream to a <code>de.uniluebeck.itm.wsn.drivers.test.MessagePacket</code>.
	 * 
	 * @param buffer The byte stream.
	 * @param offset The start of the message packet in the buffer.
	 * @param length The length of the message.
	 * @return The parsed <code>MessagePacker</code>.
	 */
	public static MessagePacket parse(final byte[] buffer, final int offset, final int length) {
		final int type = TYPE_MASK & ((int) buffer[offset]);
		// Extract message content
		final byte[] content = new byte[length - 1];
		System.arraycopy(buffer, offset + 1, content, 0, length - 1);		
		return new MessagePacket(type, content);
	}

	@Override
	public String toString() {
		return "Packet ID[" + id + "]: Type: [" + type + "], content: hex[" + StringUtils.toHexString(content) + "], string[" + new String(content) + "]";
	}

	public byte[] getContent() {
		return content;
	}

	/**
	 * Returns the current type
	 * 
	 * @return The type of the message.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns the id
	 * 
	 * @return The unique id of this message.
	 */
	public long getId() {
		return id;
	}

}