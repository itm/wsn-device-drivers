package eu.smartsantander.wsn.drivers.waspmote.operation;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractSendOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import eu.smartsantander.wsn.drivers.waspmote.WaspmoteHelper;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrame;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.request.XBeeDigiRequest;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.statusResponse.XBeeDigiStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TLMAT
 */
public class WaspmoteDigimeshSendOperation extends AbstractSendOperation {

    private static final Logger logger = LoggerFactory.getLogger(WaspmoteDigimeshSendOperation.class);

    private final WaspmoteHelper helper;
    private final int operationID;

    @Inject
    public WaspmoteDigimeshSendOperation(WaspmoteHelper helper) {
        this.helper = helper;
        this.operationID = helper.requestDeviceOperationID();
    }

    @Override
    public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
        logger.debug("Executing Digimesh send operation...");

        XBeeDigiRequest requestFrame = new XBeeDigiRequest(helper.getNodeID(), this.getMessage());
        helper.sendXBeeMessage(requestFrame, true, operationID);
        progressManager.worked(0.50f);
        XBeeFrame xbeeFrame = helper.receiveXBeeFrame(operationID, true);
        Preconditions.checkNotNull(xbeeFrame, "The node didn't reply as expected");
        XBeeDigiStatusResponse localAck = (XBeeDigiStatusResponse) xbeeFrame;
        logger.debug("localACK from node {} arrived. Delivery status: {}. Discovery status: {}. Transmit retry count:",
                new Object[]{helper.getNodeID(), localAck.getDeliveryStatus(),
                        localAck.getDiscoveryStatus(), localAck.getRetryCount()});
        progressManager.done();
        logger.debug("Send operation finished");
        return null;
    }

}
