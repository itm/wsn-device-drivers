/**********************************************************************************************************************
 * Copyright (c) 2010, Institute of Telematics, University of Luebeck                                                 *
 * All rights reserved.                                                                                               *
 *                                                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the   *
 * following conditions are met:                                                                                      *
 *                                                                                                                    *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following *
 *   disclaimer.                                                                                                      *
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the        *
 *   following disclaimer in the documentation and/or other materials provided with the distribution.                 *
 * - Neither the name of the University of Luebeck nor the names of its contributors may be used to endorse or promote*
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

package de.uniluebeck.itm.wsn.devicedrivers.sunspot;

import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wsn.devicedrivers.generic.*;
import de.uniluebeck.itm.wsn.devicedrivers.jennic.FlashType;
import de.uniluebeck.itm.wsn.devicedrivers.jennic.Sectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * sunspot
 *
 * @author Evangelos
 */
public class SunspotDevice extends iSenseDeviceImpl {

	/**
	 *
	 */
	private String nodeName;
	private long nodeId;
    private TestBed tbed;
    private boolean isGateway=false;
    private boolean rebootAfterFlashing=false;


    public SunspotDevice(String config) {
		checkNotNull(config);
		String[] strs = config.split(":");
        this.nodeName = config;
		checkArgument(!"".equals(nodeName), "The value nodeName must not be empty!");
        if (strs[3].equals("sunspotgateway")==true) this.isGateway=true;
        this.nodeName="0014.4F01.0000."+strs[4].substring(2);
        this.nodeName=this.nodeName.toUpperCase();
        try {
			this.nodeId = StringUtils.parseHexOrDecLong(strs[4]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"The last part of the node URN must be a long value. Failed to parse it..."
			);
		}
        tbed=TestBed.getInstance();
        tbed.addNode(this.nodeName,this);

	}
    public boolean isNodeGateway(){
        return this.isGateway;
    }

    @Override
	public boolean reset() throws Exception {
        if (this.isGateway){
            return true;
        }
        boolean result=tbed.resetNode(this.nodeName);
        String msg= "Rebootted:"+result+" "+this.nodeName ;
	    sendLogMessage(msg);
        return  result;
	}

    @Override
	public boolean triggerReboot() throws Exception {
   		logDebug("Starting reboot device thread");
		if (operationInProgress()) {
			logError("Already another operation in progress (" + operation + ")");
			return false;
		}
		operation = new RebootDeviceOperation(this);
		operation.setLogIdentifier(logIdentifier);
		operation.start();
		return true;
	}

    @Override
    public boolean isConnected() {
        boolean result=tbed.isNodeAlive(this.nodeName);
        String msg= "isNodeAlive:"+result+" "+this.nodeName ;
	    sendLogMessage(msg);
        return  result;
    }


    @Override
    public boolean triggerProgram(IDeviceBinFile program, boolean rebootAfterFlashing) throws Exception {
        	this.rebootAfterFlashing = rebootAfterFlashing;
		if (operationInProgress()) {
			logError("Already another operation in progress (" + operation + ")");
			return false;
		}

		operation = new FlashProgramOperation(this, program, true);
		operation.setLogIdentifier(logIdentifier);
		operation.start();
		return true;
    }

    public boolean flash(byte[] file, String name) throws Exception {
        if (this.isGateway)  return true;
        boolean result=tbed.jar_deploy(this.nodeName,file,name);
        String msg= "Flashed:"+result+" "+this.nodeName ;
	    sendLogMessage(msg);
        return  result;
	}




    @Override
    public byte[] writeFlash(int address, byte[] bytes, int offset, int len) throws Exception {
        return bytes;
    }


    @Override
	public boolean enterProgrammingMode() throws Exception {
		return true;
	}

	@Override
	public void eraseFlash() throws Exception {
		// nothing to do
	}

	@Override
	public ChipType getChipType() throws Exception {
		return ChipType.Unknown;
	}

	@Override
	public FlashType getFlashType() throws Exception {
		return FlashType.Unknown;
	}

	@Override
	public Operation getOperation() {
		if (operation == null) {
			return Operation.NONE;
		} else {
			return operation.getOperation();
		}
	}

	@Override
	public void leaveProgrammingMode() throws Exception {
		// nothing to do
	}

	@Override
	public byte[] readFlash(int address, int len) throws Exception {
		return new byte[]{};
	}




	@Override
	public void send(MessagePacket p) throws Exception {

	}

	@Override
	public void eraseFlash(Sectors.SectorIndex sector) throws Exception {
		// nothing to do
	}

	@Override
	public void shutdown() {

    }


	@Override
	public void triggerGetMacAddress(boolean rebootAfterFlashing) throws Exception {
		// nothing to do
	}


	@Override
	public void triggerSetMacAddress(MacAddress mac, boolean rebootAfterFlashing) throws Exception {
		// nothing to do
	}




	@Override
	public String toString() {
		return "SunspotDevice [" + nodeName + "]";
	}

	@Override
	public int[] getChannels() {
		return new int[]{11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
	}

	@Override
	public IDeviceBinFile loadBinFile(String fileName) {
       return null;
    }


	public void sendLogMessage(String message) {

		byte[] msgBytes = message.getBytes();
		byte[] bytes = new byte[msgBytes.length + 2];
		bytes[0] = PacketTypes.LOG;
		bytes[1] = PacketTypes.LogType.DEBUG;
		System.arraycopy(msgBytes, 0, bytes, 2, msgBytes.length);

		MessagePacket messagePacket = MessagePacket.parse(bytes, 0, bytes.length);
		logDebug("Emitting textual log message packet: {}", messagePacket);
		notifyReceivePacket(messagePacket);



	}
    public void logmsg(String msg){
        sendLogMessage(msg);
    }

	private void sendBinaryMessage(final byte binaryType, final byte[] binaryData) {

		MessagePacket messagePacket = new MessagePacket(binaryType, binaryData);

		//logDebug("Emitting binary data message packet: {}", messagePacket);
		notifyReceivePacket(messagePacket);
	}

    public String getNodeName(){
        return this.nodeName;
    }
}