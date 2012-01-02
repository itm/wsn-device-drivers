package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import com.google.inject.ImplementedBy;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeResponse;

/**
 * @author TLMAT UC
 */
@ImplementedBy(SimpleMessageHelper.class)
public interface MessageHelper {
    public enum ResponseChannelType { UPPER_LAYER, DRIVER_LAYER; }

    ResponseChannelType getResponseChannelType(AbstractXBeeResponse xbeeResponse);
}
