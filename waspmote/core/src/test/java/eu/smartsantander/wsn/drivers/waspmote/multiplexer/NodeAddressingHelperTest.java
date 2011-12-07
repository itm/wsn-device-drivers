package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrameType.XBeeProtocol;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author TLMAT UC
 */
public class NodeAddressingHelperTest {

    private static final int NODE_ID_1 = 1;

    private static final String MAC_802154_1 = "00:15:8D:00:00:04:7D:50";
    private static final String MAC_DIGI_1 = "00:15:8D:00:33:54:23:67";

    private static final int NODE_ID_2 = 2;
    private static final String MAC_802154_2 = "00:15:8D:00:00:03:68:2E";
    private static final String MAC_DIGI_2 = "00:15:8D:00:38:74:03:78";

    private static final int NODE_ID_3 = 3;
    private static final String MAC_802154_3 = "00:15:8D:00:00:05:37:9F";
    private static final String MAC_DIGI_3 = "00:15:8D:00:33:50:88:33";



    @Test
    public void testAddThenGet() throws Exception {
        NodeAddressingHelper nodeAddressingHelper = new NodeAddressingHelperMemoryImpl();
        nodeAddressingHelper.addNode(NODE_ID_1, new MacAddress(MAC_802154_1), new MacAddress(MAC_DIGI_1));
        nodeAddressingHelper.addNode(NODE_ID_2, new MacAddress(MAC_802154_2), new MacAddress(MAC_DIGI_2));

        assertEquals(MAC_802154_1,
                nodeAddressingHelper.getMACAddress(NODE_ID_1, XBeeProtocol.PROTOCOL_802154).toString());
        assertEquals(MAC_DIGI_1,
                nodeAddressingHelper.getMACAddress(NODE_ID_1, XBeeProtocol.PROTOCOL_DIGIMESH).toString());
        assertEquals(Integer.valueOf(NODE_ID_1), Integer.valueOf(
                nodeAddressingHelper.getNodeID(new MacAddress(MAC_802154_1), XBeeProtocol.PROTOCOL_802154)));
        assertEquals(Integer.valueOf(NODE_ID_1), Integer.valueOf(
                nodeAddressingHelper.getNodeID(new MacAddress(MAC_DIGI_1), XBeeProtocol.PROTOCOL_DIGIMESH)));

        assertEquals(MAC_802154_2,
                nodeAddressingHelper.getMACAddress(NODE_ID_2, XBeeProtocol.PROTOCOL_802154).toString());
        assertEquals(MAC_DIGI_2,
                nodeAddressingHelper.getMACAddress(NODE_ID_2, XBeeProtocol.PROTOCOL_DIGIMESH).toString());
        assertEquals(Integer.valueOf(NODE_ID_2), Integer.valueOf(
                nodeAddressingHelper.getNodeID(new MacAddress(MAC_802154_2), XBeeProtocol.PROTOCOL_802154)));
        assertEquals(Integer.valueOf(NODE_ID_2), Integer.valueOf(
                nodeAddressingHelper.getNodeID(new MacAddress(MAC_DIGI_2), XBeeProtocol.PROTOCOL_DIGIMESH)));
    }

    @Test
    public void testRemoveThenGetNull() throws Exception {
        NodeAddressingHelper nodeAddressingHelper = new NodeAddressingHelperMemoryImpl();
        nodeAddressingHelper.addNode(NODE_ID_1, new MacAddress(MAC_802154_1), new MacAddress(MAC_DIGI_1));
        nodeAddressingHelper.addNode(NODE_ID_2, new MacAddress(MAC_802154_2), new MacAddress(MAC_DIGI_2));

        nodeAddressingHelper.removeNode(NODE_ID_1);
        assertNull(nodeAddressingHelper.getMACAddress(NODE_ID_1, XBeeProtocol.PROTOCOL_802154));
        assertNull(nodeAddressingHelper.getMACAddress(NODE_ID_1, XBeeProtocol.PROTOCOL_DIGIMESH));
        assertNull(nodeAddressingHelper.getNodeID(new MacAddress(MAC_802154_1), XBeeProtocol.PROTOCOL_802154));
        assertNull(nodeAddressingHelper.getNodeID(new MacAddress(MAC_DIGI_1), XBeeProtocol.PROTOCOL_DIGIMESH));

        assertNotNull(nodeAddressingHelper.getMACAddress(NODE_ID_2, XBeeProtocol.PROTOCOL_802154));
        assertNotNull(nodeAddressingHelper.getMACAddress(NODE_ID_2, XBeeProtocol.PROTOCOL_DIGIMESH));
        assertNotNull(nodeAddressingHelper.getNodeID(new MacAddress(MAC_802154_2), XBeeProtocol.PROTOCOL_802154));
        assertNotNull(nodeAddressingHelper.getNodeID(new MacAddress(MAC_DIGI_2), XBeeProtocol.PROTOCOL_DIGIMESH));
    }

}
