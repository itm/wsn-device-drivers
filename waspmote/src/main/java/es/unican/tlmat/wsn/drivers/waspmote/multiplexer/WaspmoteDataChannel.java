package es.unican.tlmat.wsn.drivers.waspmote.multiplexer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import es.unican.tlmat.wsn.drivers.waspmote.frame.XBeeFrame;

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
	private HashMap<Thread, LinkedBlockingQueue<XBeeFrame>> nodeIdFrameMultiplexer;

	/**
	 * This is the NodeID identifier for this data channel
	 */
	private final int nodeID;

	/**
	 * create a new named data channel.
	 */
	private WaspmoteDataChannel(int nodeID) {
		this.nodeID = nodeID;
		nodeIdFrameMultiplexer = new HashMap<Thread, LinkedBlockingQueue<XBeeFrame>>();
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
	 * Return a data subchannel of the current data channel associated with the
	 * given consumerID, creating it if it doesn't exist.
	 */
	private LinkedBlockingQueue<XBeeFrame> getSubchannel(Thread consumerTID) {
		LinkedBlockingQueue<XBeeFrame> queue = this.nodeIdFrameMultiplexer.get(consumerTID);
		if (queue == null) {
			queue = new LinkedBlockingQueue<XBeeFrame>();
			this.nodeIdFrameMultiplexer.put(consumerTID, queue);
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
	 * @throws InterruptedException
	 */
	public void putFrame(Thread consumerTID, XBeeFrame data) throws InterruptedException {
		LinkedBlockingQueue<XBeeFrame> queue = getSubchannel(consumerTID);
		if (queue != null) {
			queue.put(data);
		}
	}

	/**
	 * Return the value in this Data Channel. If there isn't any data in the
	 * channel this call blocks.
	 *
	 * @throws InterruptedException
	 */
	public XBeeFrame getFrame(int timeout) throws InterruptedException {
		LinkedBlockingQueue<XBeeFrame> queue = getSubchannel(Thread.currentThread());
		return ((queue != null) ? queue.poll(timeout, TimeUnit.SECONDS) : null);
	}

	/**
	 * Return true if the Data channel has data waiting to be read.
	 */
	public boolean hasData() {
		LinkedBlockingQueue<XBeeFrame> queue = getSubchannel(Thread.currentThread());
		if (queue != null) {
			return ((queue.peek() != null) ? true : false);
		}
		return false;
	}

	/**
	 * Return the number of frames on the Data channel waiting to be read.
	 */
	public int getAvailableData() {
		LinkedBlockingQueue<XBeeFrame> queue = getSubchannel(Thread.currentThread());
		return ((queue != null) ? queue.size() : null);
	}

	/**
	 * Release all resources associated with a subchannel
	 */
	public synchronized void releaseSubchannel(Thread consumerTID) {
		LinkedBlockingQueue<XBeeFrame> queue = nodeIdFrameMultiplexer.remove(consumerTID);
		if (queue != null) {
			queue.clear();
		}
	}

	/**
	 * Release all resources associated with a whole channel
	 */
	public synchronized void shutDownChannel() {
		Collection<Thread> c = nodeIdFrameMultiplexer.keySet();
		Iterator<Thread> itr = c.iterator();
		while (itr.hasNext()) {
			nodeIdFrameMultiplexer.remove(itr.next()).clear();
		}
		channelsRegistry.remove(this);
	}
}
