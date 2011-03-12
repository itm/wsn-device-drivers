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

package de.uniluebeck.itm.devicedriver.jennic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.exception.ProgramChipMismatchException;
import de.uniluebeck.itm.devicedriver.util.BinDataBlock;

/**
 * @author Malte Legenhausen
 * @author dp
 */
public class JennicBinData {

	private static final Logger log = LoggerFactory.getLogger(JennicBinData.class);

	private static final int BLOCK_SIZE = 128;

	private final byte[] bytes;

	private final int length;
	
	private int blockIterator = 0;
	
	/**
	 * Constructor.
	 * 
	 * @param bytes 
	 * @param description
	 */
	public JennicBinData(byte[] bytes) {
		this.bytes = bytes;
		this.length = bytes.length;
	}

	/**
	 * Calculate number of blocks to write
	 */
	private int getFullBlocksCount() {
		return (int) (length / BLOCK_SIZE);
	}

	/**
	 * Calculate residue after last block
	 */
	private int getResidue() {
		return (int) (length % BLOCK_SIZE == 0 ? 0 : length - (getFullBlocksCount() * BLOCK_SIZE));
	}

	private int getBlockOffset(int block) {
		int maxBlocks = getBlockCount();

		if (block >= maxBlocks) {
			log.error("Block number too large (requested " + block + "/ max" + maxBlocks + ")");
			return -1;
		}

		return block * BLOCK_SIZE;
	}

	private byte[] getBlock(int block) {
		int maxBlocks = getBlockCount();

		if (block >= maxBlocks) {
			log.error("Block number too large (requested " + block + " / maxNo " + maxBlocks + ")");
			return null;
		}

		int offset = getBlockOffset(block);
		int length = (getResidue() != 0 && block == maxBlocks - 1) ? getResidue() : BLOCK_SIZE;
		byte b[] = new byte[length];
		// log.debug("Returning block #" + block + " (" + length + " bytes at position " + offset);
		System.arraycopy(bytes, offset, b, 0, length);
		return b;
	}

	private boolean hasRepeatedPattern(byte b[], int offset, int repeat, byte pattern) {

		for (int i = 0; i < repeat; ++i) {
			if (b[offset + i] != pattern) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Insert flash header of a jennic device into bin file.
	 *
	 * @param b
	 *
	 * @return
	 */
	public void insertHeader(byte[] b) throws ProgramChipMismatchException {
		final ChipType chipType = getChipType();
		int headerStart = chipType.getHeaderStart();
		int headerLength = chipType.getHeaderLength();

		if (headerStart >= 0 && headerLength > 0) {
			log.debug("Writing header for chip type " + chipType + ": " + headerLength + "bytes @ " + headerStart);
			insertAt(headerStart, headerLength, b);
			return;
		}
		throw new ProgramChipMismatchException(ChipType.UNKNOWN, chipType);
	}

	private void insertAt(int address, int len, byte[] b) {
		System.arraycopy(b, 0, bytes, address, len);
	}

	public ChipType getChipType() {
		if (bytes[0] == (byte) 0xE1) {
			log.debug("File type is JN5121");
			return ChipType.JN5121;

		} else if (hasRepeatedPattern(bytes, 0, 4, (byte) 0xE0)) {
			// log.debug("Start matches 4 x 0xE0 -> Could be JN513XR1 or JN513XR1");

			// JN513XR1
			{
				// OAD
				int start = 0x24, count = 8;
				boolean ok = hasRepeatedPattern(bytes, start, count, (byte) 0xFF);
				start += count;
				count = 4;
				ok = ok && hasRepeatedPattern(bytes, start, count, (byte) 0xF0);

				if (ok) {
					// log.debug("OAD Section found (8 x 0xFF, 4 x 0xF0)");
				} else {
					// log.debug("No OAD Section found -> not a JN513XR1");
				}

				// MAC Adress
				if (ok) {
					start += count;
					count = 32;
					ok = ok && hasRepeatedPattern(bytes, start, count, (byte) 0xFF);

					if (ok) {
						// log.debug("MAC Section found (32 x 0xFF)");
						log.debug("File type is JN513XR1");
						return ChipType.JN513XR1;
					}

				}

			}

			// JN513X
			{
				// MAC
				int start = 0x24, count = 32;
				boolean ok = hasRepeatedPattern(bytes, start, count, (byte) 0xFF);

				if (ok) {
					log.debug("File type is JN513X");
					return ChipType.JN513X;
				}
			}

		}

		log.error("Chip type is UNKNOWN");
		return ChipType.UNKNOWN;
	}

	public int getLength() {
		return length;
	}

	public BinDataBlock getNextBlock() {
		if (hasNextBlock()) {
			int offset = getBlockOffset(blockIterator);
			byte[] data = getBlock(blockIterator);

			blockIterator++;

			return new BinDataBlock(offset, data);
		} else {
			return null;
		}
	}

	public void resetBlockIterator() {
		blockIterator = 0;
	}

	public boolean hasNextBlock() {
		if (blockIterator < getBlockCount()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "JennicBinFile{" +
				"blockSize=" + BLOCK_SIZE +
				", blockIterator=" + blockIterator +
				", bytes=" + bytes +
				", length=" + length +
				'}';
	}

	public boolean isCompatible(ChipType deviceType) {
		return deviceType.equals(getChipType());
	}

	public int getBlockCount() {
		int b = getFullBlocksCount();

		if (getResidue() > 0) {
			b++;
		}

		return b;
	}

}