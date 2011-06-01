package es.unican.tlmat.wsn.drivers.waspmote.multiplexer;

import es.unican.tlmat.util.DoubleByte;
import es.unican.tlmat.util.ExtendedMacAddress;
import es.unican.tlmat.wsn.drivers.waspmote.frame.XBeeFrame;
import es.unican.tlmat.wsn.drivers.waspmote.frame.xbeeDigi.XBeeDigiRequest;
import es.unican.tlmat.wsn.drivers.waspmote.frame.xbeeDigi.XBeeDigiResponse;
import es.unican.tlmat.wsn.drivers.waspmote.frame.xbeeDigi.XBeeDigiStatusResponse;

/**
 * @author TLMAT UC
 */
public class XBeeBinaryFrameHelper {

	private static Byte frameIDcounter = 0x01;

	public static byte[] createBinaryFrame(XBeeDigiRequest xbeeDigiRequest, boolean generateLocalAck) {
		ExtendedMacAddress destinationAddress = NodeAddressingHelper.getMACAddress(xbeeDigiRequest.getNodeID16BitValue(),
				xbeeDigiRequest.getProtocol());
		if (destinationAddress == null) {
			return null;
		}
		
		return createBinaryFrame(xbeeDigiRequest, generateLocalAck, destinationAddress);
	}

    /**
     * @param xbeeDigiRequest
     * @param generateLocalAck
     * @param destinationAddress
     * @return
     */
    public static byte[] createBinaryFrame(XBeeDigiRequest xbeeDigiRequest, boolean generateLocalAck,
            ExtendedMacAddress destinationAddress) {
        byte[] payload = xbeeDigiRequest.getPayload();
		byte[] binaryFrame = new byte[payload.length + 18];
		binaryFrame[0] = (byte) 0x7E;
		DoubleByte length = new DoubleByte(payload.length + 14);
		binaryFrame[1] = length.getMsb();
		binaryFrame[2] = length.getLsb();
		binaryFrame[3] = (byte) xbeeDigiRequest.getFrameType();
		if (generateLocalAck) {
			synchronized (frameIDcounter) {
				if (frameIDcounter == (byte) 0x00) {
					frameIDcounter = (byte) 0x01;
				}
				binaryFrame[4] = frameIDcounter++;
			}
		} else {
			binaryFrame[4] = (byte) 0x00;
		}
		System.arraycopy(destinationAddress.getMacBytes(), 0, binaryFrame, 5, 8);
		binaryFrame[13] = (byte) 0xFF;
		binaryFrame[14] = (byte) 0xFE;
		binaryFrame[15] = (byte) xbeeDigiRequest.getBroadcastRadius();
		binaryFrame[16] = (byte) xbeeDigiRequest.getTransmitOptions();
		System.arraycopy(payload, 0, binaryFrame, 17, payload.length);
		binaryFrame[binaryFrame.length - 1] = calculateLibeliumChecksum(binaryFrame, 3, binaryFrame.length - 4);

		return binaryFrame;
    }

	protected static byte calculateLibeliumChecksum(byte[] binaryFrame, int pos, int length) {
		long sum = 0;
		for (int i = pos; i < pos + length; i++) {
			sum = (sum + (binaryFrame[i] & 0xff)) & 0xffffffff;
		}
		int lowByte = (int) (sum & 0x000000ff);
		byte cs = (byte) (0xff - lowByte);
		return cs;
	}

	public static XBeeDigiResponse createXBeeDigiResponse(byte[] binaryFrame) {
		int frameType = (int) binaryFrame[3] & 0x00ff;
		if (frameType == XBeeFrame.RECEIVE_PACKET) {
			ExtendedMacAddress originAddress = new ExtendedMacAddress(binaryFrame, 4);
			
			byte[] payload = new byte[binaryFrame.length - 16];
			System.arraycopy(binaryFrame, 15, payload, 0, payload.length);
			return new XBeeDigiResponse(originAddress, binaryFrame[14], payload);
		} else {
			throw new IllegalArgumentException("binaryFrame is not a Receive Packet (0x90) frame.");
		}
	}

	protected static XBeeDigiStatusResponse createXBeeDigiStatusResponse(int nodeID, byte[] binaryFrame) {
		int frameType = (int) binaryFrame[3] & 0x00ff;
		if (frameType == XBeeFrame.TRANSMIT_STATUS) {
			return new XBeeDigiStatusResponse(nodeID, binaryFrame[7], binaryFrame[8], binaryFrame[9]);
		} else {
			throw new IllegalArgumentException("binaryFrame is not a Transmit Status (0x8B) frame.");
		}
	}



}
