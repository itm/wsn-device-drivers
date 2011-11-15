package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import com.google.common.collect.HashMultimap;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeAbstractStatusResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrame;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author TLMAT UC
 */
public class WaspmoteSubchannel {

    private final LinkedBlockingQueue<XBeeFrame> incomingXbeeFrames;
    private final ConcurrentLinkedQueue<XBeeFrame> receivedXbeeFrames;

    protected WaspmoteSubchannel() {
        this.incomingXbeeFrames = new LinkedBlockingQueue<XBeeFrame>();
        this.receivedXbeeFrames = new ConcurrentLinkedQueue<XBeeFrame>();
    }

   protected synchronized void put(XBeeFrame frame) throws InterruptedException {
        incomingXbeeFrames.put(frame);
    }

    public synchronized XBeeFrame getFrame(int timeout, boolean localAck) {
        try {
            return localAck
                    ? this.pollLocalAck(timeout, TimeUnit.MILLISECONDS)
                    : this.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private synchronized XBeeFrame pollLocalAck(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        long start = System.nanoTime();
        XBeeFrame top = incomingXbeeFrames.poll(timeout, unit);
        if (top == null || top instanceof XBeeAbstractStatusResponse) {
            return top;
        } else {
            receivedXbeeFrames.add(top);
            long remainingNanos = nanos - (start - System.nanoTime());
            if (remainingNanos < 0) {
                return null;
            } else {
                return pollLocalAck(remainingNanos, TimeUnit.NANOSECONDS);
            }
        }
    }

    private synchronized XBeeFrame poll(long timeout, TimeUnit unit) throws InterruptedException {
        if(!receivedXbeeFrames.isEmpty()) {
            return receivedXbeeFrames.poll();
        } else {
            return incomingXbeeFrames.poll(timeout, unit);
        }
    }

    public synchronized boolean isEmpty() {
        return receivedXbeeFrames.isEmpty() && incomingXbeeFrames.isEmpty();
    }

    public synchronized int size() {
        return receivedXbeeFrames.size() + incomingXbeeFrames.size();
    }

    public synchronized void clear() {
        incomingXbeeFrames.clear();
        receivedXbeeFrames.clear();
    }
}
