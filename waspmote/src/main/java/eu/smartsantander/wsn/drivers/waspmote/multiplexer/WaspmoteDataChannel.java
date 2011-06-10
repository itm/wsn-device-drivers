package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrame;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author TLMAT UC
 */
/**
 * @author root
 *
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
	private HashMap<Integer, LinkedBlockingQueue<XBeeFrame>> nodeIdSubchannelLookupTable;

	/**
	 * This is the NodeID identifier for this data channel
	 */
	private final int nodeID;

	/**
	 * create a new named data channel.
	 */
	private WaspmoteDataChannel(int nodeID) {
		this.nodeID = nodeID;
		nodeIdSubchannelLookupTable = new HashMap<Integer, LinkedBlockingQueue<XBeeFrame>>();
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
	private LinkedBlockingQueue<XBeeFrame> getSubchannel(int subchannelID) {
		LinkedBlockingQueue<XBeeFrame> queue = this.nodeIdSubchannelLookupTable.get(subchannelID);
		if (queue == null) {
			queue = new LinkedBlockingQueue<XBeeFrame>();
			this.nodeIdSubchannelLookupTable.put(subchannelID, queue);
		}
		return queue;
	}

	public int getNodeID() {
		return nodeID;
	}

	/**
	 * Write a value into this data channel. All consumer threads will be
	 * notified of the availability of new data. If there are no consumer
	 * threads this is essentially a NOP.
	 *
	 * @param subchannelID
	 * @param data
	 *
	 * @throws InterruptedException
	 */
	public void putFrame(int subchannelID, XBeeFrame data) throws InterruptedException {
		LinkedBlockingQueue<XBeeFrame> queue = getSubchannel(subchannelID);
		if (queue != null) {
			queue.put(data);
		}
	}

	/**
	 * Return the value in this Data Channel. If there isn't any data in the
	 * channel this call blocks.
	 *
	 * @param subchannelID
	 * @param timeout
	 * @return
	 * @throws InterruptedException
	 */
	public XBeeFrame getFrame(int subchannelID, int timeout) throws InterruptedException {
		LinkedBlockingQueue<XBeeFrame> queue = getSubchannel(subchannelID);
		return ((queue != null) ? queue.poll(timeout, TimeUnit.SECONDS) : null);
	}

	/**
	 * Return true if the Data channel has data waiting to be read.
	 *
	 * @param subchannelID
	 */
	public boolean hasData(int subchannelID) {
		LinkedBlockingQueue<XBeeFrame> queue = getSubchannel(subchannelID);
		if (queue != null) {
			return ((queue.peek() != null) ? true : false);
		}
		return false;
	}

	/**
	 * Return the number of frames on the Data channel waiting to be read.
	 */
	public int getAvailableData(int subchannelID) {
		LinkedBlockingQueue<XBeeFrame> queue = getSubchannel(subchannelID);
		return ((queue != null) ? queue.size() : null);
	}

	/**
	 * Release all resources associated with a subchannel
	 */
	public synchronized void releaseSubchannel(int subchannelID) {
		LinkedBlockingQueue<XBeeFrame> queue = nodeIdSubchannelLookupTable.remove(subchannelID);
		if (queue != null) {
			queue.clear();
		}
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
