package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeResponse;

/**
 * @author TLMAT UC
 */
public class SimpleMessageHelper implements MessageHelper {

    @Override
    public ResponseChannelType getResponseChannelType(AbstractXBeeResponse xbeeResponse) {
        return ResponseChannelType.UPPER_LAYER;
    }
}
