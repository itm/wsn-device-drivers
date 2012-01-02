package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.util.DoubleByte;
import eu.smartsantander.util.guice.JvmSingleton;
import eu.smartsantander.wsn.drivers.waspmote.frame.XBeeBinaryFrameHelper;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeRequest;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeStatusResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrameType;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

//import eu.smartsantander.wsn.drivers.waspmote.frame.smartSantander.SmartSantanderFrame;

/**
 * @author TLMAT UC
 */
@JvmSingleton
public class WaspmoteConnectionMultiplexer implements SerialPortEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(Connection.class);

    private final HashMap<Byte, OperationAddressInfo> localAckLookupTable = new HashMap<Byte, OperationAddressInfo>();
    ;
    private final HashMap<Integer, WaspmoteSubchannel> driverLayerFramesLookupTable = new HashMap<Integer, WaspmoteSubchannel>();
    private final HashMap<Integer, OutputStream> upperLayerFramesLookupTable = new HashMap<Integer, OutputStream>();
    private final HashMap<Integer, SerialPortDataAvailableNotifier> upperLayerFramesAvailableNotifierLookupTable = new HashMap<Integer, SerialPortDataAvailableNotifier>();

    private final String port;
    private final WaspmoteMultiplexedSerialPortConnection realSerialPortConnection;
    private final NodeAddressingHelper nodeHelper;
    private final XBeeBinaryFrameHelper frameHelper;
    private final MessageHelper messageHelper;

    /**
     * Buffer to hold the whole reading
     */
    private static int READ_BUFFER_LENGTH = 256;
    private byte[] readBuffer = new byte[READ_BUFFER_LENGTH];

    /**
     * @param realSerialPortConnection
     * @param frameHelper
     */
    @Inject
    public WaspmoteConnectionMultiplexer(
            @Named("uri") String port,
            WaspmoteMultiplexedSerialPortConnection realSerialPortConnection,
            NodeAddressingHelper nodeHelper,
            XBeeBinaryFrameHelper frameHelper,
            MessageHelper messageHelper) {
        this.port = port;
        this.realSerialPortConnection = realSerialPortConnection;
        this.frameHelper = frameHelper;
        this.realSerialPortConnection.setMultiplexer(this);
        this.nodeHelper = nodeHelper;
        this.messageHelper = messageHelper;
    }

    public synchronized MultiplexedStreamInfo registerNode(int nodeID, NodeConnectionInfo nodeMACs, SerialPortDataAvailableNotifier notifier) {
        try {
            PipedOutputStream multiplexedChannelWriteStream = new PipedOutputStream();
            PipedInputStream multiplexedChannelReadStream = new PipedInputStream(multiplexedChannelWriteStream);
            upperLayerFramesLookupTable.put(nodeID, multiplexedChannelWriteStream);
            upperLayerFramesAvailableNotifierLookupTable.put(nodeID, notifier);
            realSerialPortConnection.connect(port);
            nodeHelper.addNode(nodeID, nodeMACs.getMacAddress802154(), nodeMACs.getMacAddressDigimesh());
            return new MultiplexedStreamInfo(
                    realSerialPortConnection.getOutputStream(), multiplexedChannelReadStream);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void deregisterNode(int nodeID) {
        upperLayerFramesLookupTable.remove(nodeID);
        upperLayerFramesAvailableNotifierLookupTable.remove(nodeID);
        WaspmoteDataChannel.getChannel(nodeID).shutdownChannel();
        realSerialPortConnection.shutdown(false);
        nodeHelper.removeNode(nodeID);
    }

    public synchronized boolean isAnyNodeRegistered() {
        return upperLayerFramesLookupTable.size() > 0;
    }


    public synchronized void write(AbstractXBeeRequest xbeeRequest, boolean generateLocalAck, WaspmoteSubchannel consumerSubchannel) throws IOException {
        byte[] xbeeBinaryFrame;
        xbeeBinaryFrame = frameHelper.createBinaryFrame(xbeeRequest, generateLocalAck);

        if (generateLocalAck) {
            OperationAddressInfo oai = new OperationAddressInfo(xbeeRequest.getNodeID16BitValue(), consumerSubchannel);
            localAckLookupTable.put(xbeeBinaryFrame[4], oai);
        }

        // DoubleByte cmdTypeAndReqID = new DoubleByte(xbeeRequest.getPayload()[0], xbeeRequest.getPayload()[1]);
        // syncFramesLookupTable.put(cmdTypeAndReqID.get16BitValue(), subchannelID);
        driverLayerFramesLookupTable.put(xbeeRequest.getNodeID16BitValue(), consumerSubchannel);
        // TODO With the above, only one concurrent sub-channel can coexist for each synchronous operation.
        // Use cmdTypeAndReqID instead of nodeID for adding concurrent operations support.

        realSerialPortConnection.getOutputStream().write(xbeeBinaryFrame);
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.DATA_AVAILABLE:
                processIncomingBinaryFrame();
                break;
            default:
                LOG.warn("Serial event (other than data available): " + event);
                break;
        }
    }

    private void processIncomingBinaryFrame() {
        try {
            byte[] response = readXbeeFrameFromSerialPort();
            XBeeFrameType frameType = XBeeFrameType.getXbeeFrameType(response[3]);
            switch (frameType) {
                case TRANSMIT_STATUS_DIGIMESH:
                case TRANSMIT_STATUS_802154:
                    OperationAddressInfo oai = localAckLookupTable.remove(response[4]);
                    if (oai != null) {
                        AbstractXBeeStatusResponse xbeeStatusResponse = frameHelper.createXBeeStatusResponse(oai.getNodeID(), response);
                        oai.getSubchannel().put(xbeeStatusResponse);
                    }
                    break;
                case RECEIVE_PACKET_DIGIMESH:
                case RECEIVE_PACKET_802154:
                case REMOTE_AT_CMD_RESPONSE_DIGIMESH:
                    AbstractXBeeResponse xbeeResponse = frameHelper.createXBeeResponse(response);
                    if (xbeeResponse == null) {
                        return;
                    }
                    int nodeID = xbeeResponse.getNodeID16BitValue();
                    switch (messageHelper.getResponseChannelType(xbeeResponse)) {
                        case DRIVER_LAYER:
                            // TODO evaluate the possibility of nodeID not being in lookup table (broadcast maybe?)
                            driverLayerFramesLookupTable.get(nodeID).put(xbeeResponse);
                            // Substitute nodeID with cmdTypeAndReqID if needed(see write function above)
                            break;
                        case UPPER_LAYER:
                            OutputStream multiplexedChannelWriteStream = upperLayerFramesLookupTable.get(nodeID);
                            if (multiplexedChannelWriteStream != null) {
                                // TODO not sure if payload length is needed. Used in June
                                //multiplexedChannelWriteStream.write(xbeeResponse.getPayload().length);
                                multiplexedChannelWriteStream.write(xbeeResponse.getPayload());
                                upperLayerFramesAvailableNotifierLookupTable.get(nodeID).notifyDataAvailable();
                            }
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private byte[] readXbeeFrameFromSerialPort() throws IOException {
        InputStream inputStream = this.realSerialPortConnection.getInputStream();
        int nbytes = 0;
        do {
            nbytes = inputStream.read(readBuffer, 0, 1);
        } while (readBuffer[0] != 0x7E);
        nbytes += inputStream.read(readBuffer, 1, 2);
        DoubleByte xbeePayloadSize = new DoubleByte(readBuffer[1], readBuffer[2]);
        nbytes += inputStream.read(readBuffer, 3, xbeePayloadSize.get16BitValue() + 1);
        return Arrays.copyOf(readBuffer, nbytes);
    }
}
