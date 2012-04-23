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

package de.uniluebeck.itm.wsn.drivers.telosb;

import de.uniluebeck.itm.wsn.drivers.core.util.BinaryImageBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Binary file used to program a Telos B device. The file is assumed to be in intel hex format.
 *
 * @author Friedemann Wesner
 * @author Malte Legenhausen
 */
public class TelosbBinData {

	private static final Logger log = LoggerFactory.getLogger(TelosbBinData.class);

	private final int maxBlockSize = 240 - 16;

	private BlockIterator blockIterator = new BlockIterator();

	private final List<Segment> segments = new ArrayList<Segment>();

	public TelosbBinData(byte[] binaryData) throws IOException {
		reload(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(binaryData))));
	}

	public int getBlockCount() {

		int blocksPerSegment;
		int totalBlocks = 0;

		for (Segment seg : segments) {
			blocksPerSegment = 0;
			while ((blocksPerSegment * maxBlockSize) < seg.data.length) {
				blocksPerSegment++;
			}
			totalBlocks += blocksPerSegment;
		}
		return totalBlocks;
	}

	public int getLength() {

		int totalLength = 0;

		for (Segment seg : segments) {
			totalLength += seg.data.length;
		}

		return totalLength;
	}

	public BinaryImageBlock getNextBlock() {
		BinaryImageBlock dataBlock = null;
		int actualBlockSize = maxBlockSize;
		byte[] data;
		Segment segment;

		if (hasNextBlock()) {

			segment = segments.get(blockIterator.segmentNo);

			if (blockIterator.byteNo + actualBlockSize > segment.data.length) {
				actualBlockSize = segment.data.length - blockIterator.byteNo;
			}
			data = new byte[actualBlockSize];
			System.arraycopy(segment.data, blockIterator.byteNo, data, 0, actualBlockSize);
			dataBlock = new BinaryImageBlock(segment.startAddress + blockIterator.byteNo, data);

			if (blockIterator.byteNo + maxBlockSize >= segment.data.length) {
				blockIterator.segmentNo++;
				blockIterator.byteNo = 0;
			} else {
				blockIterator.byteNo += actualBlockSize;
			}
		}

		return dataBlock;
	}

	public boolean hasNextBlock() {

		if (blockIterator.segmentNo >= segments.size()) {
			return false;
		}

		if (blockIterator.byteNo >= segments.get(blockIterator.segmentNo).data.length) {
			return false;
		}

		return true;
	}

	private void reload(BufferedReader reader) throws IOException {
		String line;
		StringTokenizer sTokenizer;
		int lineAddress;
		int currentAddress = 0;
		int segmentStartAddress = 0;
		int dataLength;
		int dataType;
		byte segmentData[] = new byte[0];
		byte tempData[];

		segments.clear();

		while ((line = reader.readLine()) != null) {

			// check for correct ihex format
			if (line.charAt(0) != ':') {
				log.error("File is not in correct intel hex format.");
				throw new IOException(
						"File is not in correct intel hex format."
				);
			}

			// remove spaces
			sTokenizer = new StringTokenizer(line, " ", false);
			line = "";
			while (sTokenizer.hasMoreElements()) {
				line += sTokenizer.nextElement();
			}

			// read data segments
			dataLength = Integer.parseInt(line.substring(1, 3), 16);
			lineAddress = Integer.parseInt(line.substring(3, 7), 16);
			dataType = Integer.parseInt(line.substring(7, 9), 16);

			if (dataType == 0x00) {
				if (currentAddress != lineAddress) {
					if (segmentData.length > 0) {
						segments.add(new Segment(segmentStartAddress,
								segmentData
						)
						);
					}
					currentAddress = lineAddress;
					segmentStartAddress = lineAddress;
					segmentData = new byte[0];
				}
				tempData = new byte[segmentData.length + dataLength];
				System.arraycopy(segmentData, 0, tempData, 0, segmentData.length);
				for (int i = 0; i < dataLength; i++) {
					tempData[segmentData.length + i] = (byte) Integer.parseInt(line.substring(9 + 2 * i, 9 + 2 * i + 2), 16);
				}
				segmentData = tempData;
				currentAddress += tempData.length;
			}
		}
		if (segmentData.length > 0) {
			segments.add(new Segment(segmentStartAddress, segmentData));
		}
	}

	public void resetBlockIterator() {
		blockIterator.reset();
	}

	public String toString() {
		return "TelosbBinFile{" + "maxBlockSize=" + maxBlockSize
				+ ", blockIterator=" + blockIterator + ", segments=" + segments
				+ '}';
	}

	/*
	 * A continued data segment of the ihex file that was read
	 */

	private class Segment {

		public int startAddress;

		public byte[] data;

		public Segment(int startAddress, byte[] data) {
			this.startAddress = startAddress;
			this.data = data;
		}
	}

	private class BlockIterator {

		int segmentNo = 0;

		int byteNo = 0;

		public void reset() {
			segmentNo = 0;
			byteNo = 0;
		}
	}
}
