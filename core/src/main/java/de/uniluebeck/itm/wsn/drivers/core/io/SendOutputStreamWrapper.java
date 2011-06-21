package de.uniluebeck.itm.wsn.drivers.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.io.Flushables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import de.uniluebeck.itm.wsn.drivers.core.async.AsyncAdapter;
import de.uniluebeck.itm.wsn.drivers.core.async.DeviceAsync;


/**
 * This wrapper allows to the write to the deviceAsync through an <code>OutputStream</code>.
 * Use the flush operation to send everything to the deviceAsync.
 * This buffer automatically flushes every 50ms.
 * 
 * @author Malte Legenhausen
 */
public class SendOutputStreamWrapper extends OutputStream {

	private class Flusher implements Runnable {
		
		private static final int DELAY = 50;
		
		private final ScheduledExecutorService executor;
		
		private long lastWriteTimeMillis = 0;
		
		private ScheduledFuture<?> future; 
		
		public Flusher(ScheduledExecutorService executor) {
			this.executor = executor;
		}
		
		@Override
		public void run() {
			if (lastWriteTimeMillis <= System.currentTimeMillis() + DELAY) {
				Flushables.flushQuietly(SendOutputStreamWrapper.this);
			}
		}
		
		public void cancel() {
			if (future != null) {
				future.cancel(true);
			}
			future = null;
		}
		
		public void flushOnTimeout() {
			lastWriteTimeMillis = System.currentTimeMillis();
			if (future == null) {
				future = executor.scheduleAtFixedRate(this, DELAY, DELAY, TimeUnit.MICROSECONDS);
			}
		}
	}
	
	/**
	 * The internal buffer size.
	 */
	private static final int BUFFER_SIZE = 2048;
	
	/**
	 * The maximum timeout for the send operation.
	 */
	private static final int SEND_TIMEOUT = 30000;
	
	private final Flusher flusher;
	
	/**
	 * The deviceAsync that is used for sending data to the deviceAsync.
	 */
	private final DeviceAsync deviceAsync;
	
	/**
	 * Internal buffer.
	 */
	private byte[] buffer = new byte[BUFFER_SIZE];
	
	private int index = 0;

	public SendOutputStreamWrapper(DeviceAsync device) {
		this(device, Executors.newScheduledThreadPool(
				1, new ThreadFactoryBuilder().setNameFormat("SendOutputStreamWrapper-Thread %d").build()
			)
		);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param deviceAsync The async deviceAsync.
	 */
	public SendOutputStreamWrapper(DeviceAsync device, ScheduledExecutorService executor) {
		this.deviceAsync = device;
		this.flusher = new Flusher(executor);
	}
	
	/**
	 * Use this method to send all written data to the deviceAsync and reset the stream.
	 */
	@Override
	public synchronized void flush() throws IOException {
		flusher.cancel();
		try {
			deviceAsync.send(buffer, SEND_TIMEOUT, new AsyncAdapter<Void>());
		} catch (RuntimeException e) {
			throw new IOException(e);
		}
		index = 0;
		buffer = new byte[BUFFER_SIZE];
	}

	@Override
	public void write(int b) throws IOException {
		buffer[index++] = (byte) b;
		if (index >= buffer.length) {
			flush();
		} else {
			flusher.flushOnTimeout();
		}
	}
	
	@Override
	public void close() throws IOException {
		flusher.cancel();
	}
}
