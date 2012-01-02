package eu.smartsantander.wsn.drivers.waspmote.operation;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractSendOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import eu.smartsantander.wsn.drivers.waspmote.WaspmoteNodeHelper;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbee802154.request.XBee802154Request;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbee802154.statusResponse.XBee802154StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TLMAT
 */
public class Waspmote802154SendOperation extends AbstractSendOperation {

    private static final Logger logger = LoggerFactory.getLogger(Waspmote802154SendOperation.class);

    private final WaspmoteNodeHelper nodeHelper;
    private final int operationID;

    @Inject
    public Waspmote802154SendOperation(WaspmoteNodeHelper nodeHelper) {
        this.nodeHelper = nodeHelper;
        this.operationID = nodeHelper.requestDeviceOperationID();
    }

    @Override
    public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
        logger.debug("Executing 802.15.4 send operation...");

        XBee802154Request requestFrame = new XBee802154Request(nodeHelper.getNodeID(), this.getMessage());
        XBee802154StatusResponse localAck = (XBee802154StatusResponse) nodeHelper.sendXBeeMessage(requestFrame, operationID);
        Preconditions.checkNotNull(localAck, "Route to IoT node has not been discovered");
        progressManager.done();

        logger.debug("Send operation finished");
        return null;
    }

}
