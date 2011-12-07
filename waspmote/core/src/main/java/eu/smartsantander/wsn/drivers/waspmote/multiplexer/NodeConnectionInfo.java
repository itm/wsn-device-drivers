package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;

/**
 * @author TLMAT UC
 */
public class NodeConnectionInfo {

    private final MacAddress macAddressDigimesh;
    private final MacAddress macAddress802154;

    /**
     * Container for a Waspmote node MAC adresses. It contains both the 802.15.4 and Digimesh MAC addresses.
     *
     * @param macAddressDigimesh Node's Digimesh interface MAC address.
     * @param macAddress802154   Node's 802.15.4 interface MAC address.
     */
    @Inject
    public NodeConnectionInfo(MacAddress macAddressDigimesh, MacAddress macAddress802154) {
        this.macAddressDigimesh = macAddressDigimesh;
        this.macAddress802154 = macAddress802154;
    }

    public MacAddress getMacAddressDigimesh() {
        return macAddressDigimesh;
    }

    public MacAddress getMacAddress802154() {
        return macAddress802154;
    }
}
