package eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbee802154.statusResponse;

import com.google.common.base.Preconditions;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeStatusResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrameType;

/**
 * @author TLMAT UC
 */
public class XBee802154StatusResponse extends AbstractXBeeStatusResponse {

	private final Status status;

	public XBee802154StatusResponse(int nodeID, byte status) {
		super(nodeID, XBeeFrameType.TRANSMIT_STATUS_802154);
		this.status = Status.getStatus(status);
        Preconditions.checkArgument(this.status != null);
	}

	public Status getStatus() {
		return this.status;
	}

	@Override
	public boolean isSuccess() {
		return status == Status.SUCCESS;
	}

}
