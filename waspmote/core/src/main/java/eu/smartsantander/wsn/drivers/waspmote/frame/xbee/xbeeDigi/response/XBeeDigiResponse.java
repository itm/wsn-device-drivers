package eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.response;

import com.google.common.base.Preconditions;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrameType;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeAbstractResponse;

/**
 * @author TLMAT UC
 */
public class XBeeDigiResponse extends XBeeAbstractResponse {

	private final ReceiveOptions receiveOptions;

	public XBeeDigiResponse(int nodeID, byte receiveOptions, byte[] payload) {
		super(nodeID, XBeeFrameType.RECEIVE_PACKET_DIGIMESH, payload);
		this.receiveOptions = ReceiveOptions.getReceiveOptions(receiveOptions);
        Preconditions.checkArgument(this.receiveOptions != null);
	}

	public ReceiveOptions getOptions() {
		return receiveOptions;
	}

}
