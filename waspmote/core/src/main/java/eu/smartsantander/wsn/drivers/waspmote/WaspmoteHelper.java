package eu.smartsantander.wsn.drivers.waspmote;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeAbstractRequest;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrame;
import eu.smartsantander.wsn.drivers.waspmote.multiplexer.WaspmoteDataChannel;
import eu.smartsantander.wsn.drivers.waspmote.multiplexer.WaspmoteSubchannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author TLMAT UC
 */
@Singleton
public class WaspmoteHelper {

    private static final Logger logger = LoggerFactory.getLogger(WaspmoteHelper.class);

    /**
     * The default timeout in millis that will be waited for available data when in synchronous mode.
     */
    private static final int DEFAULT_DATA_AVAILABLE_TIMEOUT = 30000;

    /**
     * The 16 bits node identifier for this device.
     */
    private final int nodeID;

    /**
     * <code>WaspmoteVirtualSerialPortConnection</code> for this device.
     */
    private final WaspmoteVirtualSerialPortConnection connection;

    /**
     * Used to identify all responses relatives to an operation.
     */
    private int deviceOperationID = 0;

    /**
     * Constructor.
     *
     * @param configuration The configuration of a WaspmoteDevice node, including:
     *                      - The 16 bits node identifier for this device.
     *                      - Both the 802.15.4 and Digimesh MAC addresses
     * @param connection    The virtual serial port connection for this device.
     */
    @Inject
    public WaspmoteHelper(@Named("configuration") Map<String, String> configuration, Connection connection) {
        String value = configuration.get("nodeID");
        this.nodeID = value.startsWith("0x")
                ? Integer.parseInt(value.substring(2), 16)
                : Integer.parseInt(value, 10);
        this.connection = (WaspmoteVirtualSerialPortConnection) connection;
    }

    /**
     * Getter of NodeID
     *
     * @return The 16 bits node identifier of the device
     */
    public int getNodeID() {
        return nodeID;
    }

    public synchronized int requestDeviceOperationID() {
        return deviceOperationID++;
    }

    public void sendXBeeMessage(XBeeAbstractRequest xbeeRequest, boolean generateLocalAck, int operationID) {
        connection.write(xbeeRequest, generateLocalAck, WaspmoteDataChannel.getChannel(this.nodeID).getSubchannel(operationID));
    }

    public XBeeFrame receiveXBeeFrame(int operationID,  boolean localAck) throws TimeoutException {
        return this.receiveXBeeFrame(operationID, DEFAULT_DATA_AVAILABLE_TIMEOUT, localAck);
    }

    public XBeeFrame receiveXBeeFrame(int operationID, int timeout, boolean localAck) throws TimeoutException {
        WaspmoteSubchannel subchannel = WaspmoteDataChannel.getChannel(this.nodeID).getSubchannel(operationID);
        XBeeFrame xbeeFrame = subchannel.getFrame(timeout, localAck);
        if (xbeeFrame == null) {
            throw new TimeoutException("Driver level receiveXBeeFrame() timed out.");
        }
        return xbeeFrame;
    }
}
