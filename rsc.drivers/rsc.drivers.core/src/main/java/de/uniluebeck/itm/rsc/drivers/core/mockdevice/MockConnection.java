package de.uniluebeck.itm.rsc.drivers.core.mockdevice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.io.NullOutputStream;

import de.uniluebeck.itm.rsc.drivers.core.AbstractConnection;
import de.uniluebeck.itm.rsc.drivers.core.PacketType;


/**
 * A connection that can be used for testing.
 * 
 * @author Malte Legenhausen
 */
public class MockConnection extends AbstractConnection {
	
	/**
	 * Listener interface for receiving data from the mock device.
	 * 
	 * @author Malte Legenhausen
	 */
	public interface MockListener {
		
		/**
		 * This method is called when new data is available.
		 * 
		 * @param bytes The bytes send by the <code>MockConnection</code>.
		 */
		void onData(byte[] bytes);
	}
	
	/**
	 * Internal runnable that send continuesly messages.
	 * 
	 * @author Malte Legenhausen
	 */
	private class AliveRunnable implements Runnable {
		
		/**
		 * Constant to convert milliseconds to seconds.
		 */
		private static final int MILL = 1000;
		
		/**
		 * Message count.
		 */
		private int i = 0;

		/**
		 * Start time of this runnable.
		 */
		private final Long started = Calendar.getInstance().getTimeInMillis();

		@Override
		public void run() {
			final Long diff = (Calendar.getInstance().getTimeInMillis() - started) / MILL;
			sendMessage("MockDevice alive since " + diff + " seconds (update #" + (++i) + ")");
		}
	}
	
	/**
	 * List for all <code>MockListener</code> that want receive data from this connection.
	 */
	private final List<MockListener> listeners = new ArrayList<MockListener>();
	
	/**
	 * Executor for periodically execute a given runnable.
	 */
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
	
	/**
	 * Future of the executed runnable.
	 */
	private Future<?> aliveRunnableFuture;
	
	/**
	 * The frequency with which a message has to be send.
	 */
	private final long aliveTimeout = 1;

	/**
	 * The frequency time units. 
	 */
	private final TimeUnit aliveTimeUnit = TimeUnit.SECONDS;
	
	/**
	 * Constructor.
	 */
	public MockConnection() {
		setOutputStream(new NullOutputStream());
	}
	
	/**
	 * Notify all listeners that new data is awailable.
	 * 
	 * @param bytes The data that has to be fired.
	 */
	protected void fireData(final byte[] bytes) {
		for (final MockListener listener : listeners.toArray(new MockListener[listeners.size()])) {
			listener.onData(bytes);
		}
	}
	
	/**
	 * Add a new listener for receiving data from the mock connection.
	 * 
	 * @param listener The listener that has to be added.
	 */
	public void addMockListener(final MockListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Remove a <code>MockListener</code> from this connection.
	 * 
	 * @param listener The listener that has to be removed.
	 */
	public void removeMockListener(final MockListener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void connect(final String uri) {
		setConnected(true);
		scheduleAliveRunnable();
	}

	@Override
	public void shutdown(final boolean force) {
		scheduleAliveRunnable();
		executorService.shutdown();
	}
	
	/**
	 * Start the process for sending periodically messages.
	 */
	public void scheduleAliveRunnable() {
		aliveRunnableFuture = executorService.scheduleWithFixedDelay(
			new AliveRunnable(), 
			new Random().nextInt((int) aliveTimeout), 
			aliveTimeout,
			aliveTimeUnit
		);
	}

	/**
	 * Stop the periodically sending of messages.
	 */
	public void stopAliveRunnable() {
		if (aliveRunnableFuture != null && !aliveRunnableFuture.isCancelled()) {
			aliveRunnableFuture.cancel(true);
		}
	}
	
	/**
	 * Send a message.
	 * 
	 * @param message The message that has to be send.
	 */
	public void sendMessage(final String message) {
		final byte[] msgBytes = message.getBytes();
		final byte[] bytes = new byte[msgBytes.length + 2];
		bytes[0] = (byte) PacketType.LOG.getValue();
		bytes[1] = (byte) PacketType.LogType.DEBUG.getValue();
		System.arraycopy(msgBytes, 0, bytes, 2, msgBytes.length);
		fireData(bytes);
	}
	
	/**
	 * Send pure byte data.
	 * 
	 * @param bytes The bytes that has to be send.
	 */
	public void sendData(final byte[] bytes) {
		fireData(bytes);
	}
}
