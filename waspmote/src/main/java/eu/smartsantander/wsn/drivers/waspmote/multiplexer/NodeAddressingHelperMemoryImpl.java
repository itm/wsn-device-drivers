package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrameType.XBeeProtocol;

/**
 * @author TLMAT UC
 */
public class NodeAddressingHelperMemoryImpl implements NodeAddressingHelper {

    private final BiMap<Integer, MacAddress> nodeID2MacAddress802154BiMap;
    private final BiMap<Integer, MacAddress> nodeID2MacAddressDigimeshBiMap;

    public NodeAddressingHelperMemoryImpl() {
        nodeID2MacAddress802154BiMap = HashBiMap.create();
        nodeID2MacAddressDigimeshBiMap = HashBiMap.create();
    }

    @Override
    public void addNode(int nodeID, MacAddress macAddress802154, MacAddress macAddressDigimesh) {
        nodeID2MacAddress802154BiMap.put(nodeID, macAddress802154);
        nodeID2MacAddressDigimeshBiMap.put(nodeID, macAddressDigimesh);
    }

    @Override
    public void removeNode(int nodeID) {
        nodeID2MacAddress802154BiMap.remove(nodeID);
        nodeID2MacAddressDigimeshBiMap.remove(nodeID);
    }

    @Override
    public MacAddress getMACAddress(int nodeID, XBeeProtocol protocol) {
        switch (protocol) {
            case PROTOCOL_802154:
                return nodeID2MacAddress802154BiMap.get(nodeID);
            case PROTOCOL_DIGIMESH:
                return nodeID2MacAddressDigimeshBiMap.get(nodeID);
            default:
                return null;
        }
    }

    @Override
    public Integer getNodeID(MacAddress macAddress, XBeeProtocol protocol) {
         switch (protocol) {
            case PROTOCOL_802154:
                return nodeID2MacAddress802154BiMap.inverse().get(macAddress);
            case PROTOCOL_DIGIMESH:
                return nodeID2MacAddressDigimeshBiMap.inverse().get(macAddress);
            default:
                return null;
        }

    }

}
