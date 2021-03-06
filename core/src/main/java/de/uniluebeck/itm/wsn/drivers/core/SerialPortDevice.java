package de.uniluebeck.itm.wsn.drivers.core;

import com.google.common.util.concurrent.Monitor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.uniluebeck.itm.util.concurrent.ExecutorUtils;
import de.uniluebeck.itm.wsn.drivers.core.operation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static de.uniluebeck.itm.util.StringUtils.toHexString;


/**
 * Abstract base class for device implementations.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
@Singleton
public class SerialPortDevice implements Device {

	private static final Logger log = LoggerFactory.getLogger(SerialPortDevice.class);

	protected final OutputStream pipedOutputStreamToDriverInputStream;

	protected final InputStream pipedInputStreamFromDriverOutputStream;

	protected final Connection connection;

	protected final InputStream driverInputStream;

	protected final OutputStream driverOutputStream;

	protected final OperationFactory operationFactory;

	protected ExecutorService operationExecutor;

	protected final Monitor deviceMonitor = new Monitor();

	protected final Monitor.Guard streamDataCopyStopped = new Monitor.Guard(deviceMonitor) {
		@Override
		public boolean isSatisfied() {
			return !deviceToDriverStreamDataCopyRunning && !driverToDeviceStreamDataCopyRunning;
		}
	};

	protected volatile boolean deviceToDriverStreamDataCopyRunning = false;

	protected final ConnectionListener deviceToDriverStreamDataCopyListener = new ConnectionListener() {

		private byte[] buffer = new byte[1024];

		@Override
		public void onDataAvailable(final ConnectionEvent event) {
			try {

				InputStream inputStream = connection.getInputStream();
				int bytesRead = inputStream.read(buffer);

				if (bytesRead == -1) {
					return;
				}

				if (log.isTraceEnabled()) {
					log.trace("Reading {} bytes from device stream: {}", bytesRead, toHexString(buffer, 0, bytesRead));
				}

				pipedOutputStreamToDriverInputStream.write(buffer, 0, bytesRead);
				pipedOutputStreamToDriverInputStream.flush();

			} catch (IOException e) {
				log.error("IOException while reading from device stream: {}", e);
				throw new RuntimeException(e);
			}
		}
	};

	protected ExecutorService driverToDeviceStreamDataCopyExecutor;

	protected Future<?> driverToDeviceStreamDataCopyFuture;

	protected volatile boolean driverToDeviceStreamDataCopyRunning = false;

	protected final Runnable driverToDeviceStreamDataCopyRunnable = new Runnable() {

		private byte[] buffer = new byte[1024];

		@Override
		public void run() {

			driverToDeviceStreamDataCopyRunning = true;

			int bytesRead = 0;
			while (bytesRead != -1) {

				try {

					while ((bytesRead = pipedInputStreamFromDriverOutputStream.read(buffer)) != -1) {

						if (log.isTraceEnabled()) {
							log.trace("Writing {} bytes to device stream: {}",
									bytesRead,
									toHexString(buffer, 0, bytesRead)
							);
						}

						connection.getOutputStream().write(buffer, 0, bytesRead);
						connection.getOutputStream().flush();
					}


				} catch (Exception e) {

					if (e instanceof InterruptedIOException) {
						break;
					} else {
						log.error("IOException while reading from device InputStream: {}", e);
						throw new RuntimeException(e);
					}
				}
			}

			driverToDeviceStreamDataCopyRunning = false;
		}
	};

	@Inject
	public SerialPortDevice(@Named("driverInputStream") final PipedInputStream driverInputStream,
							@Named("driverOutputStream") final PipedOutputStream driverOutputStream,
							@Named("pipedOutputStreamToDriverInputStream")
							final PipedOutputStream pipedOutputStreamToDriverInputStream,
							@Named("pipedInputStreamFromDriverOutputStream")
							final PipedInputStream pipedInputStreamFromDriverOutputStream,
							final Connection deviceConnection,
							final OperationFactory operationFactory) {

		this.driverInputStream = driverInputStream;
		this.driverOutputStream = driverOutputStream;
		this.pipedOutputStreamToDriverInputStream = pipedOutputStreamToDriverInputStream;
		this.pipedInputStreamFromDriverOutputStream = pipedInputStreamFromDriverOutputStream;
		this.connection = deviceConnection;
		this.operationFactory = operationFactory;
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
	public void close() throws IOException {

		stopStreamDataCopy();

		connection.close();
		pipedInputStreamFromDriverOutputStream.close();
		pipedOutputStreamToDriverInputStream.close();
		driverInputStream.close();
		driverOutputStream.close();

		if (driverToDeviceStreamDataCopyExecutor != null) {
			ExecutorUtils.shutdown(driverToDeviceStreamDataCopyExecutor, 1, TimeUnit.SECONDS);
		}

		if (operationExecutor != null) {
			ExecutorUtils.shutdown(operationExecutor, 1, TimeUnit.SECONDS);
		}
	}

	@Override
	public void connect(String uri) throws IOException {

		connection.connect(uri);

		operationExecutor = Executors.newSingleThreadExecutor();
		driverToDeviceStreamDataCopyExecutor = Executors.newSingleThreadExecutor();

		startStreamDataCopy();
	}

	@Override
	public boolean isConnected() {
		return connection.isConnected();
	}

	@Override
	public boolean isClosed() {
		return connection.isClosed();
	}

	public void acquireLockOnDevice() throws InterruptedException {
		if (!deviceMonitor.isOccupiedByCurrentThread()) {
			stopStreamDataCopy();
		}
		deviceMonitor.enter();
	}

	public void releaseLockOnDeviceStreams() {
		deviceMonitor.leave();
		if (!deviceMonitor.isOccupiedByCurrentThread()) {
			startStreamDataCopy();
		}
	}

	public Connection getConnection() {
		return connection;
	}

	private void startStreamDataCopy() {

		try {

			log.trace("Starting to copy between device streams and driver streams");

			deviceMonitor.enter();

			connection.addListener(deviceToDriverStreamDataCopyListener);
			deviceToDriverStreamDataCopyRunning = true;

			driverToDeviceStreamDataCopyFuture =
					driverToDeviceStreamDataCopyExecutor.submit(driverToDeviceStreamDataCopyRunnable);

		} catch (Exception e) {

			log.error("Error while starting stream data copy: {}", e);
			throw new RuntimeException(e);

		} finally {
			deviceMonitor.leave();
		}
	}

	private void stopStreamDataCopy() {

		try {

			log.trace("Stopping copying between device streams and driver streams");

			deviceMonitor.enter();

			connection.removeListener(deviceToDriverStreamDataCopyListener);
			deviceToDriverStreamDataCopyRunning = false;

			if (driverToDeviceStreamDataCopyFuture != null) {
				driverToDeviceStreamDataCopyFuture.cancel(true);
			}

			try {
				// wait for thread to stop execution
				while (!deviceMonitor.waitFor(streamDataCopyStopped, 10, TimeUnit.MILLISECONDS)) {
					// nothing to do
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

		} catch (RuntimeException e) {

			log.error("Exception while stopping stream data copy: {}", e);
			throw new RuntimeException(e);

		} finally {
			deviceMonitor.leave();
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
		operationExecutor.submit(operation);
		return operationFuture;
	}
}
