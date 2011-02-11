package de.uniluebeck.itm.devicedriver.mockdevice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.uniluebeck.itm.devicedriver.AbstractConnection;
import de.uniluebeck.itm.devicedriver.PacketType;

public class MockConnection extends AbstractConnection {
	
	public interface MockListener {
		void onData(byte[] bytes);
	}
	
	private class AliveRunnable implements Runnable {
		
		private static final int MILL = 1000;
		
		private int i = 0;

		private final Long started;

		private AliveRunnable() {
			started = Calendar.getInstance().getTimeInMillis();
		}

		@Override
		public void run() {
			final Long diff = (Calendar.getInstance().getTimeInMillis() - started) / MILL;
			sendMessage("MockDevice alive since " + diff + " seconds (update #" + (++i) + ")");
		}
	}
	
	private final List<MockListener> listeners = new ArrayList<MockListener>();
	
	/**
	 *
	 */
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
	
	/**
	 *
	 */
	private Future<?> aliveRunnableFuture;
	
	/**
	 *
	 */
	private long aliveTimeout = 1;

	/**
	 *
	 */
	private TimeUnit aliveTimeUnit = TimeUnit.SECONDS;
	
	protected void fireData(final byte[] bytes) {
		for (final MockListener listener : listeners.toArray(new MockListener[listeners.size()])) {
			listener.onData(bytes);
		}
	}
	
	public void addMockListener(final MockListener listener) {
		listeners.add(listener);
	}
	
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
	
	public void scheduleAliveRunnable() {
		aliveRunnableFuture = executorService.scheduleWithFixedDelay(
			new AliveRunnable(), 
			new Random().nextInt((int) aliveTimeout), 
			aliveTimeout,
			aliveTimeUnit
		);
	}

	public void stopAliveRunnable() {
		if (aliveRunnableFuture != null && !aliveRunnableFuture.isCancelled()) {
			aliveRunnableFuture.cancel(true);
		}
	}
	
	public void sendMessage(final String message) {
		final byte[] msgBytes = message.getBytes();
		final byte[] bytes = new byte[msgBytes.length + 2];
		bytes[0] = (byte) PacketType.LOG.getValue();
		bytes[1] = (byte) PacketType.LogType.DEBUG.getValue();
		System.arraycopy(msgBytes, 0, bytes, 2, msgBytes.length);
		fireData(bytes);
	}
	
	public void sendData(final byte[] bytes) {
		fireData(bytes);
	}
}
