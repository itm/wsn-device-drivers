package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.uniluebeck.itm.tr.util.ExecutorUtils;
import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.util.Map;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class MockDevice implements Device {

	private class MessageRunnable implements Runnable {

		private final OutputStream outputStream;

		private final byte[] messageBytes;

		private MessageRunnable(final OutputStream outputStream, final byte[] messageBytes) {
			this.outputStream = outputStream;
			this.messageBytes = messageBytes;
		}

		@Override
		public void run() {

			try {

				if (log.isTraceEnabled()) {
					log.trace("Writing message bytes {}", StringUtils.toHexString(messageBytes));
				}

				sleepIfUartLatencyConfigured();

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

	private class EchoRunnable implements Runnable {

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

						sleepIfUartLatencyConfigured();

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

	private static final String OPTION_UART_LATENCY = "UART_LATENCY";

	private static final String OPTION_BOOT_MESSAGE = "BOOT_MESSAGE";

	private static final String OPTION_BOOT_MESSAGE_TYPE = "BOOT_MESSAGE_TYPE";

	private static final String OPTION_HEARTBEAT_MESSAGE = "HEARTBEAT_MESSAGE";

	private static final String OPTION_HEARTBEAT_MESSAGE_TYPE = "HEARTBEAT_MESSAGE_TYPE";

	private static final String OPTION_HEARTBEAT_MESSAGE_RATE = "HEARTBEAT_MESSAGE_RATE";

	private static final String OPTION_ECHO = "ECHO";

	private static final Logger log = LoggerFactory.getLogger(MockDevice.class);

	private final PipedInputStream driverInputStream;

	private final PipedOutputStream driverOutputStream;

	private final PipedOutputStream pipedOutputStreamToDriverInputStream;

	private final PipedInputStream pipedInputStreamFromDriverOutputStream;

	private final OperationFactory operationFactory;

	private final Map<String, String> configuration;

	private ScheduledExecutorService scheduler;

	private ScheduledFuture<?> heartbeatSchedule;

	private ExecutorService echoExecutor;

	private Future<?> echoFuture;

	private volatile boolean connected;

	@Inject
	public MockDevice(@Named("driverInputStream") final PipedInputStream driverInputStream,
					  @Named("driverOutputStream") final PipedOutputStream driverOutputStream,
					  @Named("pipedOutputStreamToDriverInputStream")
					  final PipedOutputStream pipedOutputStreamToDriverInputStream,
					  @Named("pipedInputStreamFromDriverOutputStream")
					  final PipedInputStream pipedInputStreamFromDriverOutputStream,
					  final OperationFactory operationFactory,
					  @Named("configuration") final Map<String, String> configuration) {

		this.driverInputStream = driverInputStream;
		this.driverOutputStream = driverOutputStream;
		this.pipedOutputStreamToDriverInputStream = pipedOutputStreamToDriverInputStream;
		this.pipedInputStreamFromDriverOutputStream = pipedInputStreamFromDriverOutputStream;
		this.operationFactory = operationFactory;
		this.configuration = configuration;
	}

	@Override
	public OperationFuture<Void> eraseFlash(long timeoutMillis, @Nullable OperationListener<Void> listener) {
		log.trace("Erasing flash (timeout: " + timeoutMillis + "ms)");
		return executeOperation(operationFactory.createEraseFlashOperation(timeoutMillis, listener));
	}

	@Override
	public OperationFuture<ChipType> getChipType(long timeoutMillis, @Nullable OperationListener<ChipType> listener) {
		log.trace("Reading Chip Type (timeout: " + timeoutMillis + "ms)");
		return executeOperation(operationFactory.createGetChipTypeOperation(timeoutMillis, listener));
	}

	@Override
	public OperationFuture<Boolean> isNodeAlive(final long timeoutMillis,
												@Nullable final OperationListener<Boolean> listener) {
		log.trace("Checking if node is alive (timeout: {}ms)", timeoutMillis);
		return executeOperation(operationFactory.createIsNodeAliveOperation(timeoutMillis, listener));
	}

	@Override
	public OperationFuture<Void> program(byte[] data, long timeoutMillis, @Nullable OperationListener<Void> listener) {
		log.trace("Programming (timeout: " + timeoutMillis + "ms)");
		return executeOperation(operationFactory.createProgramOperation(data, timeoutMillis, listener));
	}

	@Override
	public OperationFuture<byte[]> readFlash(int address, int length, long timeoutMillis,
											 @Nullable OperationListener<byte[]> listener) {
		log.trace("Reading flash (address: " + address + ", length: " + length + ", timeout: " + timeoutMillis + "ms)");
		checkArgument(address >= 0, "Negative length is not allowed.");
		checkArgument(length >= 0, "Negative address is not allowed.");
		return executeOperation(operationFactory.createReadFlashOperation(address, length, timeoutMillis, listener));
	}

	@Override
	public OperationFuture<MacAddress> readMac(long timeoutMillis, @Nullable OperationListener<MacAddress> listener) {
		log.trace("Reading MAC address (timeout: " + timeoutMillis + "ms)");
		return executeOperation(operationFactory.createReadMacAddressOperation(timeoutMillis, listener));
	}

	@Override
	public OperationFuture<Void> reset(long timeoutMillis, @Nullable OperationListener<Void> listener) {
		log.trace("Resetting (timeout: " + timeoutMillis + "ms)");
		return executeOperation(operationFactory.createResetOperation(timeoutMillis, listener));
	}

	@Override
	public OperationFuture<Void> writeFlash(int address, byte[] data, int length, long timeoutMillis,
											@Nullable OperationListener<Void> listener) {
		log.trace("Writing flash (address: " + address + ", length: " + length + ", timeout: " + timeoutMillis + "ms)");
		checkArgument(address >= 0, "Negative length is not allowed.");
		checkNotNull(data, "Null data is not allowed.");
		checkArgument(length >= 0, "Negative address is not allowed.");
		return executeOperation(
				operationFactory.createWriteFlashOperation(address, data, length, timeoutMillis, listener)
		);
	}

	@Override
	public OperationFuture<Void> writeMac(MacAddress macAddress, long timeoutMillis,
										  @Nullable OperationListener<Void> listener) {
		log.trace("Writing MAC address (mac address: " + macAddress + ", timeout: " + timeoutMillis + "ms)");
		checkNotNull(macAddress, "Null MAC address is not allowed.");
		return executeOperation(operationFactory.createWriteMacAddressOperation(macAddress, timeoutMillis, listener));
	}

	@Override
	public InputStream getInputStream() {
		return driverInputStream;
	}

	@Override
	public OutputStream getOutputStream() {
		return driverOutputStream;
	}

	@Override
	public void connect(final String uri) throws IOException {

		try {

			final ThreadFactory threadFactory = new ThreadFactoryBuilder()
					.setNameFormat("MockDevice-Thread %d")
					.build();

			scheduler = Executors.newScheduledThreadPool(1, threadFactory);
			echoExecutor = Executors.newSingleThreadExecutor(threadFactory);

			startHeartBeatIfConfigured();
			startEchoIfConfigured();

			sendBootMessageIfConfigured();

		} finally {
			connected = true;
		}
	}

	@Override
	public void close() throws IOException {

		try {

			stopHeartBeatIfRunning();
			stopEchoIfRunning();

			if (scheduler != null) {
				ExecutorUtils.shutdown(scheduler, 1, TimeUnit.SECONDS);
			}

			if (echoExecutor != null) {
				ExecutorUtils.shutdown(echoExecutor, 1, TimeUnit.SECONDS);
			}

			synchronized (driverInputStream) {
				synchronized (pipedOutputStreamToDriverInputStream) {
					driverInputStream.close();
					pipedOutputStreamToDriverInputStream.close();
				}
			}

			synchronized (driverOutputStream) {
				synchronized (pipedInputStreamFromDriverOutputStream) {
					driverOutputStream.close();
					pipedInputStreamFromDriverOutputStream.close();
				}
			}

		} finally {
			connected = false;
		}
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public boolean isClosed() {
		return !isConnected();
	}

	void acquireLockOnDevice() {
		stopHeartBeatIfRunning();
		stopEchoIfRunning();
	}

	void releaseLockOnDevice() {
		startEchoIfConfigured();
		startHeartBeatIfConfigured();
	}

	void reset() {
		sleep(500);
		sendBootMessageIfConfigured();
	}

	private void sleepIfUartLatencyConfigured() {

		final String uartLatencyString = configuration.get(OPTION_UART_LATENCY);
		final Integer uartLatency = uartLatencyString == null ? null : Integer.parseInt(uartLatencyString);

		if (uartLatency != null) {
			sleep(uartLatency);
		}
	}

	private <T> OperationFuture<T> executeOperation(final Operation<T> operation) {
		final OperationFutureImpl<T> operationFuture = new OperationFutureImpl<T>(operation);
		operation.addListener(
				new OperationAdapter<T>() {

					@Override
					public void onFailure(final Throwable throwable) {
						operationFuture.setException(throwable);
					}

					@Override
					public void onSuccess(final T result) {
						operationFuture.set(result);
					}
				}
		);
		scheduler.submit(operation);
		return operationFuture;
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

			new MessageRunnable(pipedOutputStreamToDriverInputStream, bootMessageBytes).run();
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

			heartbeatSchedule = scheduler.scheduleAtFixedRate(
					new MessageRunnable(pipedOutputStreamToDriverInputStream, heartbeatMessageBytes),
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
					pipedInputStreamFromDriverOutputStream,
					pipedOutputStreamToDriverInputStream
			)
			);
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
