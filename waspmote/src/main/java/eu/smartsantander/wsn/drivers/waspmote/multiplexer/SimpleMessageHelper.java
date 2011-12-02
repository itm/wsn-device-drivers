package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeAbstractResponse;

/**
 * @author TLMAT UC
 */
public class SimpleMessageHelper implements MessageHelper {

    @Override
    public ResponseChannelType getResponseChannelType(XBeeAbstractResponse xbeeResponse) {
        return ResponseChannelType.UPPER_LAYER;
    }
}
