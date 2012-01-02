package eu.smartsantander.wsn.drivers.waspmote.operation;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractSendOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import eu.smartsantander.wsn.drivers.waspmote.WaspmoteNodeHelper;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.request.XBeeDigiRequest;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.statusResponse.XBeeDigiStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TLMAT
 */
public class WaspmoteDigimeshSendOperation extends AbstractSendOperation {

    private static final Logger logger = LoggerFactory.getLogger(WaspmoteDigimeshSendOperation.class);

    private final WaspmoteNodeHelper nodeHelper;
    private final int operationID;

    @Inject
    public WaspmoteDigimeshSendOperation(WaspmoteNodeHelper nodeHelper) {
        this.nodeHelper = nodeHelper;
        this.operationID = nodeHelper.requestDeviceOperationID();
    }

    @Override
    public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
        logger.debug("Executing Digimesh send operation...");

        XBeeDigiRequest requestFrame = new XBeeDigiRequest(nodeHelper.getNodeID(), this.getMessage());
        XBeeDigiStatusResponse localAck = (XBeeDigiStatusResponse) nodeHelper.sendXBeeMessage(requestFrame, operationID);
        Preconditions.checkNotNull(localAck, "Route to IoT node has not been discovered");
        progressManager.done();

        logger.debug("Send operation finished");
        return null;
    }

}
