package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uniluebeck.itm.tr.util.ExecutorUtils;
import de.uniluebeck.itm.tr.util.TimeDiff;
import de.uniluebeck.itm.wsn.drivers.core.AbstractConnection;

import java.io.*;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * A connection that can be used for testing.
 *
 * @author Malte Legenhausen
 */
@Singleton
public class MockConnection extends AbstractConnection {

	/**
	 * Internal runnable that sends continuously messages.
	 *
	 * @author Malte Legenhausen
	 */
	private class AliveRunnable implements Runnable {

		private TimeDiff startTime = new TimeDiff();

		private int messageCount = 0;

		private volatile boolean shutdown = false;

		@Override
		public void run() {

			sleep(new Random().nextInt((int) aliveTimeout));

			while (!shutdown) {

				if (messageCount == 0) {
					sendMessage("Booting MockDevice...");
					sleep(1000);
				}

				final String message = "MockDevice alive since " + startTime.s() + " seconds (update #" + (++messageCount) + ")";
				sendMessage(message);

				sleep(aliveTimeUnit.toMillis(aliveTimeout));
			}
		}

		public void reset() {
			startTime.touch();
			messageCount = 0;
		}
	}

	private static final byte DLE = 0x10;

	private static final byte STX = 0x02;

	private static final byte ETX = 0x03;

	/**
	 * The <code>PipedOutputStream</code>.
	 */
	private PipedOutputStream outputStream = new PipedOutputStream();

	/**
	 * The <code>PipedInputStream</code>.
	 */
	private PipedInputStream inputStream = new PipedInputStream();

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
	private final long aliveTimeout = 1;

	/**
	 * The frequency time units.
	 */
	private final TimeUnit aliveTimeUnit = TimeUnit.SECONDS;

	private final AliveRunnable aliveRunnable = new AliveRunnable();

	@Inject
	public MockConnection(MockConfiguration configuration) {
		this.configuration = configuration;
		this.executorService = Executors.newCachedThreadPool(
				new ThreadFactoryBuilder().setNameFormat("MockConnection-Thread %d").build()
		);
	}

	public void reset() {
		sleep(200);
		aliveRunnable.reset();
	}

	private void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			System.err.println(e);
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
		final OutputStream outputStream = getOutputStream();
		try {
			outputStream.write(message);
			outputStream.flush();
		} catch (IOException e) {

		}
	}

	@Override
	public void connect(final String uri) throws IOException {
		super.connect(uri);
		try {
			inputStream.connect(outputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		setOutputStream(outputStream);
		setInputStream(inputStream);
		executorService.execute(aliveRunnable);
		setConnected();
	}

	@Override
	public void close() throws IOException {
		aliveRunnable.shutdown = true;
		ExecutorUtils.shutdown(executorService, 100, TimeUnit.MILLISECONDS);
		super.close();
	}

	@Override
	public int[] getChannels() {
		return configuration.getChannels();
	}
}
