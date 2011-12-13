package eu.smartsantander.wsn.drivers.waspmote;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeFrame;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeRequest;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeStatusResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbee802154.statusResponse.XBee802154StatusResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.statusResponse.XBeeDigiStatusResponse;
import eu.smartsantander.wsn.drivers.waspmote.multiplexer.WaspmoteDataChannel;
import eu.smartsantander.wsn.drivers.waspmote.multiplexer.WaspmoteSubchannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author TLMAT UC
 */
@Singleton
public class WaspmoteNodeHelper {

    private static final Logger logger = LoggerFactory.getLogger(WaspmoteNodeHelper.class);

    /**
     * The default timeout in millis that will be waited for available data when in synchronous mode.
     */
    private static final int DEFAULT_DATA_AVAILABLE_TIMEOUT = 30000;

    /**
     * The 16 bits node identifier for this device.
     */
    private final int nodeID;

    /**
     * The 8 bytes default authentication key for this device.
     */
    private final String defaultAuthKey;

    /**
     * <code>WaspmoteVirtualSerialPortConnection</code> for this device.
     */
    private final WaspmoteVirtualSerialPortConnection connection;

    /**
     * Used to identify all responses relatives to an operation.
     */
    private int deviceOperationID = 0;

    /**
     * Used to track authKey changes over the node lifetime.
     */
    private String authKey;

    /**
     * Constructor.
     *
     * @param configuration The configuration of a WaspmoteDevice node, including:
     *                      - The 16 bits node identifier for this device.
     *                      - Both the 802.15.4 and Digimesh MAC addresses
     * @param connection    The virtual serial port connection for this device.
     */
    @Inject
    public WaspmoteNodeHelper(@Named("configuration") Map<String, String> configuration, Connection connection) {
        String value = configuration.get("nodeID");
        this.nodeID = value.startsWith("0x")
                ? Integer.parseInt(value.substring(2), 16)
                : Integer.parseInt(value, 10);
        Preconditions.checkArgument(configuration.get("authKey").length() == 8,
                "Configured authentication key length is not 8 bytes");
        this.defaultAuthKey = configuration.get("authKey");
        authKey = this.defaultAuthKey;
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

    public String resetAuthKey() {
        authKey = this.defaultAuthKey;
        return authKey;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        Preconditions.checkArgument(authKey.length() == 8,
                "Authentication key length is not 8 bytes");
        this.authKey = authKey;
    }

    public AbstractXBeeStatusResponse sendXBeeMessage(AbstractXBeeRequest xbeeRequest, int operationID) {
       return this.sendXBeeMessageWithRetries(xbeeRequest, operationID, 1);
    }

    public AbstractXBeeStatusResponse sendXBeeMessageWithRetries(AbstractXBeeRequest xbeeRequest, int operationID, int retries) {
        boolean successful = false;
        connection.write(xbeeRequest, true, WaspmoteDataChannel.getChannel(this.nodeID).getSubchannel(operationID));
        int retriesCounter = retries;
        AbstractXBeeStatusResponse localAck = null;
        while (!successful && retriesCounter > 0) {
            try {
                localAck = (AbstractXBeeStatusResponse) this.receiveXBeeFrame(operationID, DEFAULT_DATA_AVAILABLE_TIMEOUT, true);
                successful = localAck.isSuccess();
                if (localAck instanceof XBeeDigiStatusResponse) {
                    XBeeDigiStatusResponse localAckDigi = (XBeeDigiStatusResponse) localAck;
                    logger.debug("localACK from (node {}, operation {}) arrived --> (Delivery status: {}. Discovery status: {}. Transmit retry count: {})",
                            new Object[]{nodeID, operationID, localAckDigi.getDeliveryStatus(),
                                    localAckDigi.getDiscoveryStatus(), localAckDigi.getRetryCount()});
                } else {
                    XBee802154StatusResponse localAck802154 = (XBee802154StatusResponse) localAck;
                    logger.debug("localACK from (node {}, operation {}) arrived --> (Status: {})",
                            new Object[]{nodeID, operationID, localAck802154.getStatus()});
                }
            } catch (TimeoutException e) {
                logger.debug("localACK from (node {}, operation {}) was not received. Reason: {}",
                        new Object[]{nodeID, operationID, e.getMessage()});
            }
            retriesCounter--;
        }
        return successful ? localAck : null;
    }

    public AbstractXBeeResponse receiveXBeeFrame(int operationID) throws TimeoutException {
        return (AbstractXBeeResponse) this.receiveXBeeFrame(operationID, DEFAULT_DATA_AVAILABLE_TIMEOUT, false);
    }

    public AbstractXBeeResponse receiveXBeeFrame(int operationID, int timeout) throws TimeoutException {
        return (AbstractXBeeResponse) this.receiveXBeeFrame(operationID, timeout, false);
    }

    private AbstractXBeeFrame receiveXBeeFrame(int operationID, int timeout, boolean localAck) throws TimeoutException {
        WaspmoteSubchannel subchannel = WaspmoteDataChannel.getChannel(this.nodeID).getSubchannel(operationID);
        AbstractXBeeFrame xbeeFrame = subchannel.getFrame(timeout, localAck);
        if (xbeeFrame == null) {
            throw new TimeoutException("Driver level receiveXBeeFrame() timed out.");
        }
        return xbeeFrame;
    }
}
