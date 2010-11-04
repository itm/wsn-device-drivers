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

package de.uniluebeck.itm.devicedriver.pacemate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.DeviceBinData;
import de.uniluebeck.itm.devicedriver.DeviceBinDataBlock;

/**
 * @author Maick Danckwardt
 * @author Malte Legenhausen
 * @author dp
 */
public class PacemateBinData implements DeviceBinData {

	/**
	 * 
	 */
	private static final Logger log = LoggerFactory.getLogger(PacemateBinData.class);

	/**
	 * Max bytes per line in a data packet
	 */
	public final static int LINESIZE = 45;
	
	private final int blockSize = 4096;

	/**
	 * Defines the start address of the storage segment to which the data has to be written.
	 */
	private final int address;

	private int blockIterator = 0;

	/**
	 * checksum reset every 20 lines or end of block
	 */
	public long crc = 0;

	private byte[] bytes = null;

	private int length = -1;

	/**
	 * Static method to load the binary data from a file.
	 * 
	 * @param file The file from which the data has to be read.
	 * @return The JennecBinData instance with the data from the given file.
	 * @throws IOException is thrown when errors current the file operations occured.
	 */
	public static PacemateBinData fromFile(File file) throws IOException {
		if (!file.exists() || !file.canRead()) {
			log.error("Unable to open file: " + file.getAbsolutePath());
			throw new IOException("Unable to open file: " + file.getAbsolutePath());
		}
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		byte[] data = new byte[(int) file.length()];
		bis.read(data, 0, data.length);
		return new PacemateBinData(data);
	}
	
	/**
	 * Static method to load the binary data from a filename.
	 * 
	 * @param filename The filename from where the data has to be read.
	 * @return The JennicBinData instance with the data from the given filename.
	 * @throws IOException is thrown when errors current the file operations occured.
	 */
	public static PacemateBinData fromFilename(String filename) throws IOException {
		return PacemateBinData.fromFile(new File(filename));
	}
	
	/**
	 * Constructor.
	 * 
	 * @param address The startaddress at which the data has to be written.
	 * @param data
	 * @param description
	 * @throws IOException
	 */
	public PacemateBinData(int address, byte[] data) throws IOException {
		this.address = 0x3000;
		load(data);
	}
	
	public PacemateBinData(byte[] data) throws IOException {
		this(0x3000, data);
	}

	/**
	 * Loads {@code data} into {@code bytes} and fills up the last block with 0xff's.
	 *
	 * @param data
	 * @throws IOException
	 */
	private void load(byte[] data) throws IOException {

		length = 0;
		if ((int) data.length % blockSize != 0) {
			int block_count = ((int) data.length / blockSize) + 1;
			length = block_count * blockSize;
		} else {
			length = (int) data.length;
		}

		bytes = new byte[length];
		System.arraycopy(data, 0, bytes, 0, data.length);

		for (int i = data.length; i < length; i++) {
			bytes[i] = (byte) 0xff;
		}

		log.debug("Extending file 2 to " + this.length);
		log.debug("Last bytes: " + this.bytes[this.length - 2] + " " + this.bytes[this.length - 1]);
	}

	/**
	 * Calculate the CRC over the complete flash ignoring the first three sectors of the bootloader 0x3000
	 *
	 * @return crc
	 */
	public int calcCRC() {
		int crc = 0;
		int counter = 0;

		for (int i = 0; i < bytes.length; i++) {
			crc = SYS_Crc(crc, bytes[i]);
			counter++;
		}

		for (int i = bytes.length; i < (0x3FFFE - 0x3000); i++) {
			crc = SYS_Crc(crc, (byte) 0xFF);
			counter++;
		}

		return crc;
	}

	private int SYS_Crc(int crc, byte ser_data) {
		crc = ((crc >> 8) & 0xff) | ((crc << 8) & 0xff00);
		crc ^= ser_data & 0xff;
		crc ^= (crc & 0xff) >> 4;
		crc ^= ((crc << 8) << 4) & 0xffff;
		crc ^= ((crc & 0xff) << 4) << 1;

		return crc;
	}

	/**
	 * Calculate number of blocks to write
	 */
	private int getFullBlocksCount() {
		return (int) (length / blockSize);
	}

	/**
	 * Calculate residue after last block
	 */
	private int getResidue() {
		return (int) (length % blockSize == 0 ? 0 : length - (getFullBlocksCount() * blockSize));
	}

	private int getBlockOffset(int block) {
		int maxBlocks = getBlockCount();

		if (block >= maxBlocks) {
			log.error("Block number too large (requested " + block + "/ max" + maxBlocks + ")");
			return -1;
		}

		return block * blockSize;
	}

	private byte[] getBlock(int block) {
		int maxBlocks = getBlockCount();

		if (block >= maxBlocks) {
			log.error("Block number too large (requested " + block + " / maxNo " + maxBlocks + ")");
			return null;
		}

		int offset = getBlockOffset(block);
		int length = (getResidue() != 0 && block == maxBlocks - 1) ? getResidue() : blockSize;
		int array_length = length;
		if ((length % 4) != 0) {
			array_length = (((int) (length / 4)) + 1) * 4;
		} // needs to be multiple of 4
		byte b[] = new byte[array_length];
		b[b.length - 1] = 0;
		b[b.length - 2] = 0;
		b[b.length - 3] = 0;
		// log.debug("Returning block #" + block + " (" + length + " bytes at position " + offset);
		System.arraycopy(bytes, offset, b, 0, length);
		return b;
	}

	/**
	 * @param b
	 */
	public void calcChecksum(byte b) {
		crc = crc + ((int) b & 0xFF);
		// System.out.print(((int) b & 0xFF) +" crc "+crc+" I ");
	}

	/**
	 * @param b
	 */
	public static int calcCRCChecksum(int crc, byte b) {
		crc = crc + ((int) b & 0xFF);
		// System.out.print(((int) b & 0xFF) +" crc "+crc+" I ");
		return crc;
	}

	/**
	 * @param b
	 *
	 * @return
	 */
	public static byte encodeByte(byte b) {
		if (b == 0) {
			return 0x60;
		} else {
			return (byte) (b + 0x20);
		}
	}

	/**
	 * encodes the bytes to be send to the lpc2136 from bin to uucode
	 *
	 * @param data
	 * @param realDataLength
	 *
	 * @return
	 */
	public byte[] encode(byte[] data, int realDataLength) {
		// length of the uu encoded stream 3 bytes hex => 4 bytes uucode + 1 byte legth real length
		int array_length = ((data.length / 3) * 4) + 1;
		byte[] outbuf = new byte[array_length];

		outbuf[0] = (byte) ((realDataLength) + 32);

		int internCounter = 1;

		// System.out.print(outbuf[0]+"");

		for (int i = 0; i + 2 < data.length; i = i + 3) {
			// calc CRC
			calcChecksum(data[i]);
			calcChecksum(data[i + 1]);
			calcChecksum(data[i + 2]);

			/* Encode 3 bytes from the input buffer */

			outbuf[internCounter] = encodeByte((byte) ((data[i] & 0xFC) >> 2));
			outbuf[internCounter + 1] = encodeByte((byte) (((data[i] & 0x03) << 4) + ((data[i + 1] & 0xF0) >> 4)));
			outbuf[internCounter + 2] = encodeByte((byte) (((data[i + 1] & 0x0F) << 2) + ((data[i + 2] & 0xC0) >> 6)));
			outbuf[internCounter + 3] = encodeByte((byte) (data[i + 2] & 0x3F));

			// System.out.print(""+outbuf[internCounter]+""+outbuf[internCounter +1]+""+outbuf[internCounter
			// +2]+""+outbuf[internCounter +3]);
			internCounter = internCounter + 4;
		}
		// System.out.println("");

		return outbuf;
	}

	/**
	 * encodes the bytes to be send to the lpc2136 from bin to uucode
	 *
	 * @param data
	 * @param realDataLength
	 *
	 * @return
	 */
	public static byte[] encodeCRCData(byte[] data, int realDataLength) {
		// length of the uu encoded stream 3 bytes hex => 4 bytes uucode + 1 byte legth real length
		int array_length = ((data.length / 3) * 4) + 1;
		byte[] outbuf = new byte[array_length];

		outbuf[0] = (byte) ((realDataLength) + 32);

		int internCounter = 1;

		// System.out.print(outbuf[0]+"");

		for (int i = 0; i + 2 < data.length; i = i + 3) {
			/* Encode 3 bytes from the input buffer */

			outbuf[internCounter] = encodeByte((byte) ((data[i] & 0xFC) >> 2));
			outbuf[internCounter + 1] = encodeByte((byte) (((data[i] & 0x03) << 4) + ((data[i + 1] & 0xF0) >> 4)));
			outbuf[internCounter + 2] = encodeByte((byte) (((data[i + 1] & 0x0F) << 2) + ((data[i + 2] & 0xC0) >> 6)));
			outbuf[internCounter + 3] = encodeByte((byte) (data[i + 2] & 0x3F));

			// System.out.print(""+outbuf[internCounter]+""+outbuf[internCounter +1]+""+outbuf[internCounter
			// +2]+""+outbuf[internCounter +3]);
			internCounter = internCounter + 4;
		}
		// System.out.println("");

		return outbuf;
	}

	/**
	 * Insert flash header of a jennic device into bin file.
	 *
	 * @param b
	 *
	 * @return
	 */
	public boolean insertHeader(byte[] b) {
		ChipType chipType = getChipType();
		int headerStart = chipType.getHeaderStart();
		int headerLength = chipType.getHeaderLength();

		if (headerStart >= 0 && headerLength > 0) {
			log.debug("Writing header for chip type " + chipType + ": " + headerLength + "bytes @ " + headerStart);
			insertAt(headerStart, headerLength, b);
			return true;
		}

		log.error("Unknown chip type");
		return false;
	}

	private void insertAt(int address, int len, byte[] b) {
		System.arraycopy(b, 0, bytes, address, len);
	}

	@Override
	public ChipType getChipType() {
		return ChipType.LPC2136;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public DeviceBinDataBlock getNextBlock() {
		if (hasNextBlock()) {
			int offset = address + getBlockOffset(blockIterator);
			byte[] data = getBlock(blockIterator);

			blockIterator++;

			return new DeviceBinDataBlock(offset, data);
		} else {
			return null;
		}
	}

	@Override
	public void resetBlockIterator() {
		blockIterator = 0;
	}

	@Override
	public boolean hasNextBlock() {
		if (blockIterator < getBlockCount()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "PacemateBinFile{" +
				"blockSize=" + blockSize +
				", startAddress=" + address +
				", blockIterator=" + blockIterator +
				", crc=" + crc +
				", bytes=" + bytes +
				", length=" + length +
				'}';
	}

	@Override
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