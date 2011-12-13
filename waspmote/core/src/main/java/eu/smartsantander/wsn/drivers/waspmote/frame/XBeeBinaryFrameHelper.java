package eu.smartsantander.wsn.drivers.waspmote.frame;

import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.util.DoubleByte;
import de.uniluebeck.itm.wsn.drivers.core.util.HexUtils;
import eu.smartsantander.util.guice.JvmSingleton;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeRequest;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeStatusResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrameType;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbee802154.request.XBee802154Request;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbee802154.response.XBee802154Response;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbee802154.statusResponse.XBee802154StatusResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.at.XBeeRemoteATCmdRequest;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.at.XBeeRemoteATCmdResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.request.XBeeDigiRequest;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.response.XBeeDigiResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.statusResponse.XBeeDigiStatusResponse;
import eu.smartsantander.wsn.drivers.waspmote.multiplexer.NodeAddressingHelper;

import java.util.Arrays;

/**
 * @author TLMAT UC
 */
@JvmSingleton
public class XBeeBinaryFrameHelper {

    private static final MacAddress BROADCAST_MAC_ADDRESS = new MacAddress(HexUtils.hexString2ByteArray("00:00:00:00:00:00:FF:FF"));

    private final NodeAddressingHelper nodeHelper;
    private Byte frameIDcounter = 0x01;

    @Inject
    public XBeeBinaryFrameHelper(NodeAddressingHelper nodeHelper) {
        this.nodeHelper = nodeHelper;
    }


    public byte[] createBinaryFrame(AbstractXBeeRequest xbeeRequest, boolean generateLocalAck) {
        MacAddress destinationAddress;
        DoubleByte nodeID = xbeeRequest.getNodeID();
        if (nodeID.getLsb() == (byte) 0x00FF) {
            destinationAddress = BROADCAST_MAC_ADDRESS;
        } else {
            destinationAddress = nodeHelper.getMACAddress(xbeeRequest.getNodeID16BitValue(), xbeeRequest.getFrameType().protocol());
        }
        if (destinationAddress == null) {
            return null;
        }

        switch (xbeeRequest.getFrameType()) {
            case TRANSMIT_REQUEST_DIGIMESH:
                return createDigiBinaryFrame((XBeeDigiRequest) xbeeRequest, generateLocalAck, destinationAddress);
            case REMOTE_AT_CMD_REQUEST_DIGIMESH:
                return createRemoteATBinaryFrame((XBeeRemoteATCmdRequest) xbeeRequest, generateLocalAck, destinationAddress);
            case TRANSMIT_REQUEST_802154:
                return create802154BinaryFrame((XBee802154Request) xbeeRequest, generateLocalAck, destinationAddress);
            default:
                return null;
        }
    }

    /**
     * @param xbeeDigiRequest
     * @param generateLocalAck
     * @param destinationAddress
     * @return
     */
    private byte[] createDigiBinaryFrame(XBeeDigiRequest xbeeDigiRequest, boolean generateLocalAck, MacAddress destinationAddress) {
        byte[] payload = xbeeDigiRequest.getPayload();
        byte[] binaryFrame = new byte[payload.length + 18];
        binaryFrame[0] = (byte) 0x7E;
        DoubleByte length = new DoubleByte(payload.length + 14);
        binaryFrame[1] = length.getMsb();
        binaryFrame[2] = length.getLsb();
        binaryFrame[3] = xbeeDigiRequest.getFrameType().value();
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
        System.arraycopy(destinationAddress.toByteArray(), 0, binaryFrame, 5, 8);
        binaryFrame[13] = (byte) 0xFF;
        binaryFrame[14] = (byte) 0xFE;
        binaryFrame[15] = (byte) xbeeDigiRequest.getBroadcastRadius();
        binaryFrame[16] = (byte) xbeeDigiRequest.getTransmitOptions();
        System.arraycopy(payload, 0, binaryFrame, 17, payload.length);
        binaryFrame[binaryFrame.length - 1] = calculateLibeliumChecksum(binaryFrame, 3, binaryFrame.length - 4);

        return binaryFrame;
    }

    /**
     * @param xbee802154Request
     * @param generateLocalAck
     * @param destinationAddress
     * @return
     */
    private byte[] create802154BinaryFrame(XBee802154Request xbee802154Request, boolean generateLocalAck, MacAddress destinationAddress) {
        byte[] payload = xbee802154Request.getPayload();
        byte[] binaryFrame = new byte[payload.length + 15];
        binaryFrame[0] = (byte) 0x7E;
        DoubleByte length = new DoubleByte(payload.length + 11);
        binaryFrame[1] = length.getMsb();
        binaryFrame[2] = length.getLsb();
        binaryFrame[3] = xbee802154Request.getFrameType().value();
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
        System.arraycopy(destinationAddress.toByteArray(), 0, binaryFrame, 5, 8);
        binaryFrame[13] = (byte) xbee802154Request.getOptions();
        System.arraycopy(payload, 0, binaryFrame, 14, payload.length);
        binaryFrame[binaryFrame.length - 1] = calculateLibeliumChecksum(binaryFrame, 3, binaryFrame.length - 4);

        return binaryFrame;
    }

    /**
     * @param xbeeRemoteATCmdRequest
     * @param generateLocalAck
     * @param destinationAddress
     * @return
     */
    private byte[] createRemoteATBinaryFrame(XBeeRemoteATCmdRequest xbeeRemoteATCmdRequest, boolean generateLocalAck, MacAddress destinationAddress) {
        byte[] payload = xbeeRemoteATCmdRequest.getPayload();
        byte[] binaryFrame = new byte[payload.length + 19];
        binaryFrame[0] = (byte) 0x7E;
        DoubleByte length = new DoubleByte(payload.length + 15);
        binaryFrame[1] = length.getMsb();
        binaryFrame[2] = length.getLsb();
        binaryFrame[3] = xbeeRemoteATCmdRequest.getFrameType().value();
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
        System.arraycopy(destinationAddress.toByteArray(), 0, binaryFrame, 5, 8);
        binaryFrame[13] = (byte) 0xFF;
        binaryFrame[14] = (byte) 0xFE;
        binaryFrame[15] = (byte) xbeeRemoteATCmdRequest.getRemoteCommandOptions();
        byte[] atCommandNameBytes = xbeeRemoteATCmdRequest.getCommandName().getBytes();
        binaryFrame[16] = atCommandNameBytes[0];
        binaryFrame[17] = atCommandNameBytes[1];
        System.arraycopy(payload, 0, binaryFrame, 18, payload.length);
        binaryFrame[binaryFrame.length - 1] = calculateLibeliumChecksum(binaryFrame, 3, binaryFrame.length - 4);

        return binaryFrame;
    }

    private byte calculateLibeliumChecksum(byte[] binaryFrame, int pos, int length) {
        long sum = 0;
        for (int i = pos; i < pos + length; i++) {
            sum = (sum + (binaryFrame[i] & 0xff)) & 0xffffffff;
        }
        int lowByte = (int) (sum & 0x000000ff);
        byte cs = (byte) (0xff - lowByte);
        return cs;
    }

    public AbstractXBeeResponse createXBeeResponse(byte[] binaryFrame) {
        switch (XBeeFrameType.getXbeeFrameType(binaryFrame[3])) {
            case RECEIVE_PACKET_DIGIMESH:
                return this.createXBeeDigiResponse(binaryFrame);
            case REMOTE_AT_CMD_RESPONSE_DIGIMESH:
                return this.createXBeeRemoteATResponse(binaryFrame);
            case RECEIVE_PACKET_802154:
                return this.createXBee802154Response(binaryFrame);
            default:
                throw new IllegalArgumentException("binaryFrame is not a Receive Packet (0x80, 0x90 or 0x97) frame.");
        }
    }

    private XBeeDigiResponse createXBeeDigiResponse(byte[] binaryFrame) {
        MacAddress originAddress = new MacAddress(binaryFrame, 4);
        Integer nodeID = this.nodeHelper.getNodeID(originAddress, XBeeFrameType.RECEIVE_PACKET_DIGIMESH.protocol());
        if (nodeID == null) {
            return null;
        }
        byte[] payload = Arrays.copyOfRange(binaryFrame, 15, binaryFrame.length - 1);
        return new XBeeDigiResponse(nodeID.intValue(), binaryFrame[14], payload);
    }

    private XBeeRemoteATCmdResponse createXBeeRemoteATResponse(byte[] binaryFrame) {
        MacAddress originAddress = new MacAddress(binaryFrame, 4);
        Integer nodeID = this.nodeHelper.getNodeID(originAddress, XBeeFrameType.RECEIVE_PACKET_DIGIMESH.protocol());
        if (nodeID == null) {
            return null;
        }
        String commandName = String.valueOf(binaryFrame[15]) + String.valueOf(binaryFrame[16]);
        byte[] commandData = Arrays.copyOfRange(binaryFrame, 18, binaryFrame.length - 1);
        return new XBeeRemoteATCmdResponse(nodeID.intValue(), commandName, binaryFrame[17], commandData);
    }

    private XBee802154Response createXBee802154Response(byte[] binaryFrame) {
        //TODO Revisar si o si.
//        MacAddress originAddress = new MacAddress(binaryFrame, 18);
        String mac = new String(Arrays.copyOfRange(binaryFrame, 28, binaryFrame.length - 1));
        MacAddress originAddress = new MacAddress(HexUtils.hexString2ByteArray(mac));
        Integer nodeID = this.nodeHelper.getNodeID(originAddress, XBeeFrameType.RECEIVE_PACKET_802154.protocol());
        if (nodeID == null) {
            return null;
        }
        // El 14 es el principio del RF Data
        byte[] payload = Arrays.copyOfRange(binaryFrame, 14, binaryFrame.length - 1);
        return new XBee802154Response(nodeID, binaryFrame[12], binaryFrame[13], payload);
    }

    public AbstractXBeeStatusResponse createXBeeStatusResponse(int nodeID, byte[] binaryFrame) {
        switch (XBeeFrameType.getXbeeFrameType(binaryFrame[3])) {
            case TRANSMIT_STATUS_DIGIMESH:
                return new XBeeDigiStatusResponse(nodeID, binaryFrame[7], binaryFrame[8], binaryFrame[9]);
            case TRANSMIT_STATUS_802154:
                return new XBee802154StatusResponse(nodeID, binaryFrame[5]);
            default:
                throw new IllegalArgumentException("binaryFrame is not a Transmit Status (0x89 or 0x8B) frame.");
        }
    }

}
