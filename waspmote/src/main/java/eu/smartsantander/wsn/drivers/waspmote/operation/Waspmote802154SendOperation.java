package eu.smartsantander.wsn.drivers.waspmote.operation;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import eu.smartsantander.wsn.drivers.waspmote.WaspmoteHelper;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbee802154.statusResponse.XBee802154StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractSendOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrame;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbee802154.request.XBee802154Request;

/**
 * @author TLMAT
 */
public class Waspmote802154SendOperation extends AbstractSendOperation {

	private static final Logger logger = LoggerFactory.getLogger(Waspmote802154SendOperation.class);

	private final WaspmoteHelper helper;
	private final int operationID;

    @Inject
	public Waspmote802154SendOperation(WaspmoteHelper helper) {
		this.helper = helper;
		this.operationID = helper.requestDeviceOperationID();
	}

	@Override
	public Void run(ProgressManager progressManager, OperationContext context) throws Exception {
		logger.debug("Executing 802.15.4 send operation...");

        XBee802154Request requestFrame = new XBee802154Request(helper.getNodeID(), this.getMessage());
		helper.sendXBeeMessage(requestFrame, true, operationID);
		progressManager.worked(0.50f);
		XBeeFrame xbeeFrame = helper.receiveXBeeFrame(operationID, true);
        Preconditions.checkNotNull(xbeeFrame, "The node didn't reply as expected");
        XBee802154StatusResponse localAck = (XBee802154StatusResponse) xbeeFrame;
        logger.debug("localACK from node {} arrived. Status: {}", helper.getNodeID(), localAck.getStatus());
        progressManager.done();
        logger.debug("Send operation finished");
        return null;
	}

}
