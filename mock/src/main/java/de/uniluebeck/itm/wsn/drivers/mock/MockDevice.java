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

		private final MockConnection connection;

		private final byte[] messageBytes;

		private MessageRunnable(final MockConnection connection, final byte[] messageBytes) {
			this.connection = connection;
			this.messageBytes = messageBytes;
		}

		@Override
		public void run() {
			connection.writeUpstreamBytes(messageBytes);
		}

	}

	private static class EchoRunnable implements Runnable {

		private final MockConnection connection;

		private final InputStream inputStream;

		private EchoRunnable(final MockConnection connection, final InputStream inputStream) {
			this.connection = connection;
			this.inputStream = inputStream;
		}

		@Override
		public void run() {

			log.trace("MockDevice.echoRunnable started!");

			try {

				byte[] b = new byte[1024];
				int read;

				synchronized (inputStream) {

					while ((read = inputStream.read(b)) != -1) {
						log.trace("MockDevice.echoRunnable echoing {} bytes", read);
						connection.writeUpstreamBytes(b, 0, read);
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

	private static final String OPTION_BOOT_MESSAGE = MockDevice.class.getName() + "/bootMessage";

	private static final String OPTION_BOOT_MESSAGE_TYPE = MockDevice.class.getName() + "/bootMessageType";

	private static final String OPTION_HEARTBEAT_MESSAGE = MockDevice.class.getName() + "/heartbeatMessage";

	private static final String OPTION_HEARTBEAT_MESSAGE_TYPE = MockDevice.class.getName() + "/heartbeatMessageType";

	private static final String OPTION_HEARTBEAT_MESSAGE_RATE = MockDevice.class.getName() + "/heartbeatMessageType";

	private static final String OPTION_ECHO = MockDevice.class.getName() + "/echo";

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

			new MessageRunnable(mockConnection, bootMessageBytes).run();
		}
	}

	private void startHeartBeatIfConfigured() {

		String heartbeatMessage = configuration.get(OPTION_HEARTBEAT_MESSAGE);
		String heartbeatMessageType = configuration.get(OPTION_HEARTBEAT_MESSAGE_TYPE);
		String heartbeatMessageRate = configuration.get(OPTION_HEARTBEAT_MESSAGE_RATE);

		if (heartbeatMessage != null) {

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
					new MessageRunnable(mockConnection, heartbeatMessageBytes),
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
			heartbeatSchedule.cancel(true);
			heartbeatSchedule = null;
		}
	}

	private void startEchoIfConfigured() {

		final String echoString = configuration.get(OPTION_ECHO);
		final boolean echo = echoString == null || Boolean.parseBoolean(echoString);

		if (echo) {
			echoFuture = echoExecutor.submit(new EchoRunnable(mockConnection, pipeInputStreamFromDriverOutputStream));
		}
	}

	private void stopEchoIfRunning() {

		if (echoFuture != null) {
			echoFuture.cancel(true);
			echoFuture = null;
		}
	}
}
