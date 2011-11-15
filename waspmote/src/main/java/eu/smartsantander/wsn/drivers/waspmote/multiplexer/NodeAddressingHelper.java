package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrameType;

/**
 * @author TLMAT UC
 */
public interface NodeAddressingHelper {
    void addNode(int nodeID, MacAddress macAddress802154, MacAddress macAddressDigimesh);

    void removeNode(int nodeID);

    MacAddress getMACAddress(int nodeID, XBeeFrameType.XBeeProtocol protocol);

    Integer getNodeID(MacAddress macAddress, XBeeFrameType.XBeeProtocol protocol);
}
