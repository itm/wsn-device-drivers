package de.uniluebeck.itm.wsn.drivers.mock;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.AbstractConnection;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionEvent;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionListener;


/**
 * A connection that can be used for testing.
 * 
 * @author Malte Legenhausen
 */
public class MockConnection extends AbstractConnection implements ConnectionListener {
	
	/**
	 * The <code>PipedOutputStream</code>.
	 */
	private PipedOutputStream outputStream;
	
	/**
	 * The <code>PipedInputStream</code>.
	 */
	private PipedInputStream inputStream;
	
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
		
		/**
		 * The mock device for sending the device.
		 */
		private final MockConnection device;
		
		/**
		 * Constructor.
		 * 
		 * @param device A MockDevice instance.
		 */
		public AliveRunnable(final MockConnection device) {
			this.device = device;
		}

		@Override
		public void run() {
			final Long diff = (Calendar.getInstance().getTimeInMillis() - started) / MILL;
			final String message = "MockDevice alive since " + diff + " seconds (update #" + (++i) + ")";
			device.sendMessage(message);
		}
	}
	
	private final MockConfiguration configuration;
	
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
	
	@Inject
	public MockConnection(MockConfiguration configuration) {
		this.configuration = configuration;
		addListener(this);
	}
	
	/**
	 * Start the process for sending periodically messages.
	 */
	public void scheduleAliveRunnable() {
		aliveRunnableFuture = executorService.scheduleWithFixedDelay(
			new AliveRunnable(this), 
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
	 * Writing a message to the output stream.
	 * 
	 * @param message The message as string.
	 */
	public void sendMessage(final String message) {
		sendMessage(message.getBytes());		
	}
	
	/**
	 * Writing a message to the output stream.
	 * 
	 * @param message The message as byte array.
	 */
	public void sendMessage(final byte[] message) {
		final OutputStream outputStream = getOutputStream();
		try {
			outputStream.write(message);
			outputStream.flush();
		} catch (IOException e) {
			
		}
	}

	@Override
	public void onConnectionChange(ConnectionEvent event) {
		if (event.isConnected()) {
			scheduleAliveRunnable();
		} else {
			scheduleAliveRunnable();
			executorService.shutdown();
		}
	}
	
	@Override
	public void connect(final String uri) {
		outputStream = new PipedOutputStream();
		try {
			inputStream = new PipedInputStream(outputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		setOutputStream(outputStream);
		setInputStream(inputStream);
		setConnected(true);
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		setConnected(false);
	}
	
	@Override
	public int[] getChannels() {
		return configuration.getChannels();
	}
}
