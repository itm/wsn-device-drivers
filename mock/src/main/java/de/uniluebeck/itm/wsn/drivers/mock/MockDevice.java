package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uniluebeck.itm.tr.util.ExecutorUtils;
import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.SerialPortDevice;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.*;

public class MockDevice extends SerialPortDevice {

	private static class MessageRunnable implements Runnable {

		private final OutputStream outputStream;

		private final byte[] messageBytes;

		private MessageRunnable(final OutputStream outputStream, final byte[] messageBytes) {
			this.outputStream = outputStream;
			this.messageBytes = messageBytes;
		}

		@Override
		public void run() {

			try {

				synchronized (outputStream) {
					outputStream.write(messageBytes);
					outputStream.flush();
				}

			} catch (IOException e) {
				log.error("IOException while writing to MockConnection.inputStreamPipedOutputStream: {}", e);
				throw new RuntimeException(e);
			}
		}

	}

	private static class EchoRunnable implements Runnable {

		private final InputStream inputStream;

		private final OutputStream outputStream;

		private EchoRunnable(final InputStream inputStream, final OutputStream outputStream) {
			this.inputStream = inputStream;
			this.outputStream = outputStream;
		}

		@Override
		public void run() {

			log.trace("MockDevice.echoRunnable started!");

			try {

				byte[] b = new byte[1024];
				int read;

				synchronized (inputStream) {

					while ((read = inputStream.read(b)) != -1) {

						if (log.isTraceEnabled()) {
							log.trace("MockDevice.echoRunnable echoing {} bytes: {}", read, new String(b, 0, read));
						}

						try {

							synchronized (outputStream) {
								outputStream.write(b, 0, read);
								outputStream.flush();
							}

						} catch (IOException e) {
							log.error("IOException while writing to outputStream: {}", e);
							throw new RuntimeException(e);
						}
					}
				}

			} catch (Exception e) {
				if (e instanceof InterruptedIOException) {
					// expected when shutting down
				} else {
					log.error("Exception in MockDevice.echoRunnable: {}", e);
					throw new RuntimeException(e);
				}
			}
		}

	}

	private static final String OPTION_BOOT_MESSAGE = "BOOT_MESSAGE";

	private static final String OPTION_BOOT_MESSAGE_TYPE = "BOOT_MESSAGE_TYPE";

	private static final String OPTION_HEARTBEAT_MESSAGE = "HEARTBEAT_MESSAGE";

	private static final String OPTION_HEARTBEAT_MESSAGE_TYPE = "HEARTBEAT_MESSAGE_TYPE";

	private static final String OPTION_HEARTBEAT_MESSAGE_RATE = "HEARTBEAT_MESSAGE_RATE";

	private static final String OPTION_ECHO = "ECHO";

	private static final Logger log = LoggerFactory.getLogger(MockDevice.class);

	private final MockConnection mockConnection;

	private final Map<String, String> configuration;

	private ScheduledExecutorService heartbeatScheduler;

	private ScheduledFuture<?> heartbeatSchedule;

	private ExecutorService echoExecutor;

	private Future<?> echoFuture;

	@Inject
	public MockDevice(@Named("driverInputStream") final InputStream driverInputStream,
					  @Named("driverOutputStream") final OutputStream driverOutputStream,
					  @Named("pipeOutputStreamToDriverInputStream")
					  final OutputStream pipeOutputStreamToDriverInputStream,
					  @Named("pipeInputStreamFromDriverOutputStream")
					  final InputStream pipeInputStreamFromDriverOutputStream,
					  final Connection deviceConnection,
					  final OperationFactory operationFactory,
					  final MockConnection mockConnection,
					  @Named("configuration") final Map<String, String> configuration) {

		super(driverInputStream, driverOutputStream, pipeOutputStreamToDriverInputStream,
				pipeInputStreamFromDriverOutputStream, deviceConnection, operationFactory
		);

		this.mockConnection = mockConnection;
		this.configuration = configuration;
	}

	@Override
	public void close() throws IOException {

		super.close();

		if (isClosed()) {

			stopHeartBeatIfRunning();
			stopEchoIfRunning();

			if (heartbeatScheduler != null) {
				ExecutorUtils.shutdown(heartbeatScheduler, 10, TimeUnit.SECONDS);
			}

			if (echoExecutor != null) {
				ExecutorUtils.shutdown(echoExecutor, 10, TimeUnit.SECONDS);
			}
		}
	}

	@Override
	public void connect(final String uri) throws IOException {

		super.connect(uri);

		if (isConnected()) {

			final ThreadFactory threadFactory = new ThreadFactoryBuilder()
					.setNameFormat("MockDevice-Thread %d")
					.build();

			heartbeatScheduler = Executors.newScheduledThreadPool(1, threadFactory);
			echoExecutor = Executors.newSingleThreadExecutor(threadFactory);

			startHeartBeatIfConfigured();
			startEchoIfConfigured();
		}
	}

	@Override
	public void releaseLockOnDeviceStreams() {
		super.releaseLockOnDeviceStreams();
		startHeartBeatIfConfigured();
	}

	@Override
	public void acquireLockOnDevice() throws InterruptedException {
		stopHeartBeatIfRunning();
		super.acquireLockOnDevice();
	}

	void reset() {

		stopHeartBeatIfRunning();
		stopEchoIfRunning();

		sleep(500);

		sendBootMessageIfConfigured();
		startEchoIfConfigured();
		startHeartBeatIfConfigured();
	}

	private void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void sendBootMessageIfConfigured() {

		String bootMessage = configuration.get(OPTION_BOOT_MESSAGE);
		String bootMessageType = configuration.get(OPTION_BOOT_MESSAGE_TYPE);

		if (bootMessage != null) {

			if (bootMessageType == null) {
				bootMessageType = "ascii";
			}

			final byte[] bootMessageBytes = parseMessageBytes(bootMessage, bootMessageType);

			new MessageRunnable(mockConnection.getInputStreamPipedOutputStream(), bootMessageBytes).run();
		}
	}

	private void startHeartBeatIfConfigured() {

		String heartbeatMessage = configuration.get(OPTION_HEARTBEAT_MESSAGE);
		String heartbeatMessageType = configuration.get(OPTION_HEARTBEAT_MESSAGE_TYPE);
		String heartbeatMessageRate = configuration.get(OPTION_HEARTBEAT_MESSAGE_RATE);

		if (heartbeatMessage != null) {

			log.debug("Starting heartbeat");

			if (heartbeatMessageType == null) {
				heartbeatMessageType = "ascii";
			}

			if (heartbeatMessageRate == null) {
				heartbeatMessageRate = "10000";
			}

			int heartbeatMessageRateMillis;
			try {
				heartbeatMessageRateMillis = Integer.parseInt(heartbeatMessageRate);
			} catch (NumberFormatException e) {
				throw new RuntimeException(
						"HEARTBEAT_MESSAGE_RATE value \"" + heartbeatMessageRate + "\" can't be parsed as an Integer!"
				);
			}

			byte[] heartbeatMessageBytes = parseMessageBytes(heartbeatMessage, heartbeatMessageType);

			heartbeatSchedule = heartbeatScheduler.scheduleAtFixedRate(
					new MessageRunnable(mockConnection.getInputStreamPipedOutputStream(), heartbeatMessageBytes),
					heartbeatMessageRateMillis,
					heartbeatMessageRateMillis,
					TimeUnit.MILLISECONDS
			);
		}
	}

	private byte[] parseMessageBytes(final String message, final String messageType) {
		byte[] heartbeatMessageBytes;
		if ("ascii".equals(messageType)) {
			heartbeatMessageBytes = message.getBytes();
		} else if ("binary".equals(messageType)) {
			heartbeatMessageBytes = StringUtils.fromStringToByteArray(message);
		} else {
			throw new RuntimeException("Unknown HEARTBEAT_MESSAGE_TYPE value \"" + messageType + "\"");
		}
		return heartbeatMessageBytes;
	}

	private void stopHeartBeatIfRunning() {

		if (heartbeatSchedule != null) {
			log.debug("Stopping heartbeat");
			heartbeatSchedule.cancel(true);
			heartbeatSchedule = null;
		}
	}

	private void startEchoIfConfigured() {

		final String echoString = configuration.get(OPTION_ECHO);
		final boolean echo = echoString == null || Boolean.parseBoolean(echoString);

		if (echo) {
			log.debug("Starting echo runnable");
			echoFuture = echoExecutor.submit(new EchoRunnable(
					mockConnection.getOutputStreamPipedInputStream(),
					mockConnection.getInputStreamPipedOutputStream()
			));
		}
	}

	private void stopEchoIfRunning() {

		if (echoFuture != null) {
			log.debug("Stopping echo runnable");
			echoFuture.cancel(true);
			echoFuture = null;
		}
	}
}
