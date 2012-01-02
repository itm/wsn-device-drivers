package eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.statusResponse;

import com.google.common.base.Preconditions;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeStatusResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrameType;

/**
 * @author TLMAT UC
 */
public class XBeeDigiStatusResponse extends AbstractXBeeStatusResponse {

	private final int retryCount;
	private final DeliveryStatus deliveryStatus;
	private final DiscoveryStatus discoveryStatus;

	public XBeeDigiStatusResponse(int nodeID, int retryCount, byte deliveryStatus, byte discoveryStatus) {
		super(nodeID, XBeeFrameType.TRANSMIT_STATUS_DIGIMESH);
		this.retryCount = retryCount;
		this.deliveryStatus = DeliveryStatus.getDeliveryStatus(deliveryStatus);
        Preconditions.checkArgument(this.deliveryStatus != null);
		this.discoveryStatus = DiscoveryStatus.getDiscoveryStatus(discoveryStatus);
        Preconditions.checkArgument(this.discoveryStatus != null);
	}

	public int getRetryCount() {
		return retryCount;
	}

	public DeliveryStatus getDeliveryStatus() {
		return deliveryStatus;
	}

	public DiscoveryStatus getDiscoveryStatus() {
		return discoveryStatus;
	}

	@Override
	public boolean isSuccess() {
		return deliveryStatus == DeliveryStatus.SUCCESS;
	}

}
