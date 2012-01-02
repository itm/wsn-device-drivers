package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeStatusResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.AbstractXBeeFrame;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author TLMAT UC
 */
public class WaspmoteSubchannel {

    private final LinkedBlockingQueue<AbstractXBeeFrame> incomingXbeeFrames;
    private final ConcurrentLinkedQueue<AbstractXBeeFrame> receivedXbeeFrames;

    protected WaspmoteSubchannel() {
        this.incomingXbeeFrames = new LinkedBlockingQueue<AbstractXBeeFrame>();
        this.receivedXbeeFrames = new ConcurrentLinkedQueue<AbstractXBeeFrame>();
    }

   protected synchronized void put(AbstractXBeeFrame frame) throws InterruptedException {
        incomingXbeeFrames.put(frame);
    }

    public synchronized AbstractXBeeFrame getFrame(int timeout, boolean localAck) {
        try {
            return localAck
                    ? this.pollLocalAck(timeout, TimeUnit.MILLISECONDS)
                    : this.poll(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private synchronized AbstractXBeeFrame pollLocalAck(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        long start = System.nanoTime();
        AbstractXBeeFrame top = incomingXbeeFrames.poll(timeout, unit);
        if (top == null || top instanceof AbstractXBeeStatusResponse) {
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

    private synchronized AbstractXBeeFrame poll(long timeout, TimeUnit unit) throws InterruptedException {
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
