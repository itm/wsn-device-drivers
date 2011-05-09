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

package de.uniluebeck.itm.wsn.drivers.isense;

/**
 * Contains constants to identify the packet types ({@link de.uniluebeck.itm.wsn.devicedrivers.generic.MessagePacket#getType()}.
 */
public enum PacketType {
	
	ISENSE_ISHELL_INTERPRETER(0),
	
	RESET(1),
	
	SERAERIAL(2),
	
	TIMERESPONSE(3),
	
	CAMERA_APPLICATION(4),
	
	FUNCTIONTEST(4),
	
	AMR_APPLICATION(5),
	
	ACC_APPLICATION(6),
	
	OUT_VIRTUAL_RADIO(7),
	
	OUT_RESERVED_2(8),
	
	OUT_RESERVED_3(9),
	
	CUSTOM_OUT_1(10),
	
	CUSTOM_OUT_2(11),
	
	CUSTOM_OUT_3(12),
	
	HIBERNATION(5),
	
	OTAP(6),
	
	DATA_EXCHANGER(25),
	
	LOG(104),
	
	PLOT(105),
	
	FLASH_DUMP(106),
	
	PLOTX(107),
	
	JPEG(108),

	TIMEREQUEST(109),
	
	AUDIO(110),
	
	/**
	 * UART Message Type for incoming SpyGlass Packets
	 */
	SPYGLASS(111),
	
	FLOATBUFFER(112),
	
	ISENSE_ISI_PACKET_TYPE_ISENSE_ID(113),

	/**
	 * UART Message Type for incoming virtual radio communication from the node
	 */
	IN_VIRTUAL_RADIO(114),
	
	TOS_AMTYPE_PRINTF(100);
	
	private final int value;
	
	private PacketType(final int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	/**
	 * Corresponds to the first byte of the {@link de.uniluebeck.itm.wsn.devicedrivers.generic.MessagePacket#getContent()}
	 */
	public enum LogType {

		DEBUG(0),

		FATAL(1);
		
		private final int value;
		
		private LogType(final int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}

	public enum ISenseCommands {

		ISENSE_ISI_COMMAND_SET_CHANNEL(2),

		ISENSE_ISI_COMMAND_SEND_ID_TO_ISHELL(3),

		ISENSE_ISI_COMMAND_ISHELL_TO_ROUTING(4);
		
		
		private final int value;
		
		private ISenseCommands(final int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public enum ISenseRoutings {

		ISENSE_ISI_ROUTING_TREE_ROUTING(7);
		
		private final int value;
		
		private ISenseRoutings(final int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}
}
