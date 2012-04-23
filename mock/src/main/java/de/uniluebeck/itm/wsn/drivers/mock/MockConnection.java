package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uniluebeck.itm.tr.util.ExecutorUtils;
import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.tr.util.TimeDiff;
import de.uniluebeck.itm.wsn.drivers.core.AbstractConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * A connection that can be used for testing.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
@Singleton
public class MockConnection extends AbstractConnection {

	/**
	 * Internal runnable that sends continuously messages.
	 *
	 * @author Malte Legenhausen
	 * @author Daniel Bimschas
	 */
	private class AliveRunnable implements Runnable {

		private TimeDiff startTime = new TimeDiff();

		private int messageCount = 0;

		private volatile boolean shutdown = false;

		@Override
		public void run() {

			sendMessage("Booting MockDevice...");

			sleep(new Random().nextInt((int) aliveTimeUnit.toMillis(aliveTimeout)));

			while (!shutdown) {

				final String message =
						"MockDevice alive since " + startTime.s() + " seconds (update #" + (++messageCount) + ")";
				sendMessage(message);

				sleep(aliveTimeUnit.toMillis(aliveTimeout));
			}
		}
	}

	private static final byte DLE = 0x10;

	private static final byte STX = 0x02;
	private static final byte ETX = 0x03;

	private static final Logger log = LoggerFactory.getLogger(MockConnection.class);

	/**
	 * The {@link OutputStream} that is being written to from users of this device.
	 */
	private final PipedOutputStream outputStream = new PipedOutputStream();

	/**
	 * The {@link InputStream} instance from which the mock device can read data that was written to {@link
	 * MockConnection#outputStream}.
	 */
	private final PipedInputStream outputStreamPipedInputStream = new PipedInputStream();

	/**
	 * The {@link InputStream} instance from which is being read from users of this device..
	 */
	private final PipedInputStream inputStream = new PipedInputStream();

	/**
	 * The {@link OutputStream} to which the mock device can write data that will then be piped to {@link
	 * MockConnection#inputStream}.
	 */
	private final PipedOutputStream inputStreamPipedOutputStream = new PipedOutputStream();

	/**
	 * Configuration for this device.
	 */
	private final MockConfiguration configuration;

	/**
	 * Executor for periodically execute a given runnable.
	 */
	private final ExecutorService executorService;

	/**
	 * The frequency with which a message has to be send.
	 */
	private final long aliveTimeout = 10;

	/**
	 * The frequency time units.
	 */
	private final TimeUnit aliveTimeUnit = TimeUnit.SECONDS;

	private AliveRunnable aliveRunnable;

	private final Runnable echoRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				byte[] b = new byte[1024];
				int read;
				synchronized (outputStreamPipedInputStream) {

					while ((read = outputStreamPipedInputStream.read(b)) != -1) {

						log.trace("MockConnection.echoRunnable echoing {} bytes", read);

						synchronized (inputStreamPipedOutputStream) {
							inputStreamPipedOutputStream.write(b, 0, read);
							inputStreamPipedOutputStream.flush();
							signalDataAvailable();
						}
					}
				}
			} catch (Exception e) {
				if (e instanceof InterruptedIOException) {
					// expected when shutting down
				} else {
					log.error("Exception in MockConnection.echoRunnable: {}", e);
					throw new RuntimeException(e);
				}
			}
		}
	};

	private Future<?> echoRunnableFuture;

	@Inject
	public MockConnection(MockConfiguration configuration) {
		this.configuration = configuration;
		this.executorService = Executors.newCachedThreadPool(
				new ThreadFactoryBuilder().setNameFormat("MockConnection-Thread %d").build()
		);
	}

	public void reset() {
		sleep(200);
		stopAliveRunnable();
		startAliveRunnable();
	}

	private void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			log.error("InterruptedException while sleeping: {}", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Writing a message to the output stream.
	 *
	 * @param message
	 * 		The message as string.
	 */
	public void sendMessage(final String message) {
		sendMessage(encapsulateWithDleStxEtx(message.getBytes()));
	}

	private byte[] encapsulateWithDleStxEtx(byte[] src) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(src.length + 4);
		outputStream.write(DLE);
		outputStream.write(STX);
		for (byte b : src) {
			if (b == DLE) {
				outputStream.write(DLE);
			}
			outputStream.write(b);
		}
		outputStream.write(DLE);
		outputStream.write(ETX);
		return outputStream.toByteArray();
	}

	/**
	 * Writing a message to the output stream.
	 *
	 * @param message
	 * 		The message as byte array.
	 */
	public void sendMessage(final byte[] message) {

		if (log.isTraceEnabled()) {
			log.trace("Sending message {}", StringUtils.toHexString(message));
		}

		try {

			synchronized (inputStreamPipedOutputStream) {
				inputStreamPipedOutputStream.write(message);
				inputStreamPipedOutputStream.flush();
				signalDataAvailable();
			}

		} catch (IOException e) {
			log.error("IOException while writing to MockConnection.inputStreamPipedOutputStream: {}", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void connect(final String uri) throws IOException {

		super.connect(uri);

		synchronized (inputStreamPipedOutputStream) {
			inputStream.connect(inputStreamPipedOutputStream);
		}

		synchronized (outputStreamPipedInputStream) {
			outputStream.connect(outputStreamPipedInputStream);
		}

		setOutputStream(outputStream);
		setInputStream(inputStream);

		startAliveRunnable();
		startEchoRunnable();

		setConnected();
	}

	@Override
	public void close() throws IOException {
		stopAliveRunnable();
		ExecutorUtils.shutdown(executorService, 100, TimeUnit.MILLISECONDS);
		super.close();
	}

	@Override
	public int[] getChannels() {
		return configuration.getChannels();
	}

	public void startAliveRunnable() {
		aliveRunnable = new AliveRunnable();
		executorService.execute(aliveRunnable);
	}

	private void startEchoRunnable() {
		echoRunnableFuture = executorService.submit(echoRunnable);
	}

	public void stopAliveRunnable() {
		if (aliveRunnable != null) {
			aliveRunnable.shutdown = true;
			aliveRunnable = null;
		}
	}

	private void stopEchoRunnable() {
		if (echoRunnableFuture != null && !(echoRunnableFuture.isDone() || echoRunnableFuture.isCancelled())) {
			echoRunnableFuture.cancel(true);
			echoRunnableFuture = null;
		}
	}
}
