package es.unican.tlmat.wsn.drivers.waspmote.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import es.unican.tlmat.wsn.drivers.util.HexUtils;
import es.unican.tlmat.wsn.drivers.waspmote.WaspmoteDevice;
import es.unican.tlmat.wsn.drivers.waspmote.frame.XBeeFrame;
import es.unican.tlmat.wsn.drivers.waspmote.frame.xbeeDigi.XBeeDigiRequest;
import es.unican.tlmat.wsn.drivers.waspmote.frame.xbeeDigi.XBeeDigiResponse;
import es.unican.tlmat.wsn.drivers.waspmote.frame.xbeeDigi.XBeeDigiStatusResponse;

/**
 * @author TLMAT UC
 */
public class WaspmoteReadIdOperation extends AbstractOperation<MacAddress> implements ReadMacAddressOperation {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(WaspmoteReadIdOperation.class);

	WaspmoteDevice device;

	public WaspmoteReadIdOperation(WaspmoteDevice device) {
		this.device = device;
	}

	@Override
	public MacAddress execute(ProgressManager progressManager) throws Exception {
		LOG.debug("Read NodeID Operation in progress...");
		byte[] payload = HexUtils.hexString2ByteArray("ab 01 23 01 00 13 a2 00 40 68 6d c0");
		XBeeDigiRequest xBeeDigiRequest = new XBeeDigiRequest(device.getNodeID(), payload);
		device.sendXBeeMessage(xBeeDigiRequest, true);
		progressManager.worked(0.25f);
		XBeeFrame xbeeFrame;
		xbeeFrame = device.receiveXBeeFrame();
		if (xbeeFrame instanceof XBeeDigiStatusResponse) {
			LOG.debug("localACK from node " + device.getNodeID() + " arrived");
			progressManager.worked(0.25f);
			XBeeDigiStatusResponse status = (XBeeDigiStatusResponse) xbeeFrame;
			if (status.isSuccess()) {
				xbeeFrame = device.receiveXBeeFrame();
				if (xbeeFrame instanceof XBeeDigiResponse) {
					LOG.debug("Response from node " + device.getNodeID() + " arrived");
					XBeeDigiResponse response = (XBeeDigiResponse) xbeeFrame;
					return new MacAddress(response.getPayload());
				}
			}
		}
		return null;
	}

}
