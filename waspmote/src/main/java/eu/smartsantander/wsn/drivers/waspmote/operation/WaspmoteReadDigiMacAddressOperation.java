package eu.smartsantander.wsn.drivers.waspmote.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.exception.UnexpectedResponseException;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.util.HexUtils;
import eu.smartsantander.wsn.drivers.waspmote.WaspmoteDevice;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrame;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.XBeeDigiRequest;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.XBeeDigiResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.XBeeDigiStatusResponse;

/**
 * @author TLMAT UC
 */
public class WaspmoteReadDigiMacAddressOperation extends AbstractOperation<MacAddress> implements
		ReadMacAddressOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(WaspmoteReadDigiMacAddressOperation.class);

	private final WaspmoteDevice device;
	private final int operationID;

	public WaspmoteReadDigiMacAddressOperation(WaspmoteDevice device, int operationID) {
		this.device = device;
		this.operationID = operationID;
	}

	@Override
	public MacAddress execute(ProgressManager progressManager) throws Exception {
		LOG.debug("Read NodeID Operation in progress...");
		// byte[] payload = HexUtils.hexString2ByteArray("AB");
		byte[] payload = HexUtils.hexString2ByteArray("AB 01 23 01 00 13 A2 00 40 68 6D C0");
		XBeeDigiRequest xBeeDigiRequest = new XBeeDigiRequest(device.getNodeID(), payload);
		device.sendXBeeMessage(xBeeDigiRequest, true, operationID);
		progressManager.worked(0.25f);
		XBeeFrame xbeeFrame;
		xbeeFrame = device.receiveXBeeFrame(operationID);
		if (xbeeFrame instanceof XBeeDigiStatusResponse) {
			LOG.debug("localACK from node " + device.getNodeID() + " arrived");
			progressManager.worked(0.25f);
			XBeeDigiStatusResponse status = (XBeeDigiStatusResponse) xbeeFrame;
			if (status.isSuccess()) {
				xbeeFrame = device.receiveXBeeFrame(operationID);
				if (xbeeFrame instanceof XBeeDigiResponse) {
					LOG.debug("Response from node " + device.getNodeID() + " arrived");
					XBeeDigiResponse response = (XBeeDigiResponse) xbeeFrame;
					return new MacAddress(response.getPayload(), 4);
				}
			}
		}
		throw new UnexpectedResponseException("The node didn't reply as expected", 1, 0);
	}

}
