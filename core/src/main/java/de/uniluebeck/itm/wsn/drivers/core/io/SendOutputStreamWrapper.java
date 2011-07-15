package de.uniluebeck.itm.wsn.drivers.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.common.io.Flushables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.OperationAdapter;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.operation.MaxMessageLength;


/**
 * This wrapper allows to the write to the deviceAsync through an <code>OutputStream</code>.
 * Use the flush operation to send everything to the deviceAsync.
 * This buffer automatically flushes every 50ms.
 * 
 * @author Malte Legenhausen
 */
public class SendOutputStreamWrapper extends OutputStream implements Runnable {
	
	private static final Logger LOG = Logger.getLogger(SendOutputStreamWrapper.class);
	
	/**
	 * The delay between each try to flush.
	 */
	private static final int DELAY = 50;
	
	/**
	 * The internal buffer size.
	 */
	private static final int DEFAULT_MAX_LENGTH = 150;
	
	/**
	 * The maximum timeout for the send operation.
	 */
	private static final int SEND_TIMEOUT = 30000;
	
	/**
	 * The deviceAsync that is used for sending data to the deviceAsync.
	 */
	private final Device deviceAsync;
	
	/**
	 * The executor that scheduled the flush of the buffer.
	 */
	private final ScheduledExecutorService executor;
	
	/**
	 * The future associated with the automatic flush.
	 */
	private final ScheduledFuture<?> future; 
	
	private int maxLength = DEFAULT_MAX_LENGTH;
	
	/**
	 * Internal buffer.
	 */
	private byte[] buffer = new byte[maxLength];
	
	/**
	 * The current buffer position.
	 */
	private int index = 0;
	
	/**
	 * The last write on the buffer.
	 */
	private long lastWriteTimeMillis = 0;

	/**
	 * Constructor.
	 * 
	 * @param device The device that is used for the send.
	 */
	public SendOutputStreamWrapper(Device device) {
		this(device, Executors.newScheduledThreadPool(
				1, new ThreadFactoryBuilder().setNameFormat("SendOutputStreamWrapper-Thread %d").build()
			)
		);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param deviceAsync The async deviceAsync.
	 * @param executor The executor that is used for flushing the internal buffer automatically.
	 */
	@Inject
	public SendOutputStreamWrapper(Device device, ScheduledExecutorService executor) {
		this.deviceAsync = device;
		this.executor = executor;
		this.future = this.executor.scheduleAtFixedRate(this, DELAY, DELAY, TimeUnit.MICROSECONDS);
	}
	
	/**
	 * Apply new maximum length after a flush.
	 * 
	 * @param maxLength The new max length of a message send at the same moment.
	 */
	@Inject(optional = true)
	public void setMaxLength(@MaxMessageLength int maxLength) {
		this.maxLength = maxLength;
		Flushables.flushQuietly(this);
	}
	
	/**
	 * Use this method to send all written data to the deviceAsync and reset the stream.
	 */
	@Override
	public void flush() throws IOException {
		synchronized (buffer) {
			try {
				deviceAsync.send(buffer, SEND_TIMEOUT, new OperationAdapter<Void>());
			} catch (RuntimeException e) {
				throw new IOException(e);
			}
			index = 0;
			buffer = new byte[maxLength];
		}
	}

	@Override
	public void write(int b) throws IOException {
		synchronized (buffer) {
			buffer[index++] = (byte) b;
			lastWriteTimeMillis = System.currentTimeMillis();
			if (index >= buffer.length) {
				flush();
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		future.cancel(true);
	}
	
	@Override
	public void run() {
		if (index > 0 && lastWriteTimeMillis + DELAY <= System.currentTimeMillis()) {
			LOG.trace("Delay was reached. Automatic flushing.");
			Flushables.flushQuietly(this);
		}
	}
}
