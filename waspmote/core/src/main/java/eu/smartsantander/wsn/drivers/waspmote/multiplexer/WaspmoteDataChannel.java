package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @author TLMAT UC
 */
public class WaspmoteDataChannel {

    /**
     * This is the registry of all Data Channels that are currently open.
     */
    private static HashMap<Integer, WaspmoteDataChannel> channelsRegistry = null;

    /**
     * Each data channel has a hash table of threads that are consuming events
     * for this channel.
     */
    private HashMap<Integer, WaspmoteSubchannel> nodeIdSubchannelLookupTable;

    /**
     * This is the NodeID identifier for this data channel
     */
    private final int nodeID;

    /**
     * create a new named data channel.
     */
    private WaspmoteDataChannel(int nodeID) {
        this.nodeID = nodeID;
        nodeIdSubchannelLookupTable = new HashMap<Integer, WaspmoteSubchannel>();
    }

    /**
     * Return a data channel from the registry associated with the given ndeID,
     * creating it if it doesn't exist.
     */
    public static synchronized WaspmoteDataChannel getChannel(int nodeID) {
        if (channelsRegistry == null) {
            channelsRegistry = new HashMap<Integer, WaspmoteDataChannel>();
        }
        WaspmoteDataChannel channel = (WaspmoteDataChannel) channelsRegistry.get(nodeID);
        if (channel == null) {
            channel = new WaspmoteDataChannel(nodeID);
            channelsRegistry.put(nodeID, channel);
        }
        return channel;
    }

    /**
     * Return the complete set of channels associated with devices
     */
    public static Set<Integer> getChannelSet() {
        return channelsRegistry.keySet();
    }

    /**
     * Return a data sub-channel of the current data channel associated with the
     * given subchannelID, creating it if it doesn't exist.
     *
     * @param subchannelID
     */
    public WaspmoteSubchannel getSubchannel(int subchannelID) {
        WaspmoteSubchannel queue = this.nodeIdSubchannelLookupTable.get(subchannelID);
        if (queue == null) {
            queue = new WaspmoteSubchannel();
            this.nodeIdSubchannelLookupTable.put(subchannelID, queue);
        }
        return queue;
    }

    public int getNodeID() {
        return nodeID;
    }

    /**
     * Release all resources associated with a subchannel
     */
    public synchronized void releaseSubchannel(int subchannelID) {
        WaspmoteSubchannel queue = nodeIdSubchannelLookupTable.remove(subchannelID);
        Preconditions.checkNotNull(queue);
        queue.clear();
    }

    /**
     * Release all resources associated with a whole channel
     */
    public synchronized void shutdownChannel() {
        Collection<Integer> c = nodeIdSubchannelLookupTable.keySet();
        Iterator<Integer> itr = c.iterator();
        while (itr.hasNext()) {
            nodeIdSubchannelLookupTable.remove(itr.next()).clear();
        }
        channelsRegistry.remove(nodeID);
    }
}
