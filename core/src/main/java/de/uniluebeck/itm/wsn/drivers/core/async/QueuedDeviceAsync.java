package de.uniluebeck.itm.wsn.drivers.core.async;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.*;
import de.uniluebeck.itm.wsn.drivers.core.event.StateChangedEvent;
import de.uniluebeck.itm.wsn.drivers.core.io.SendOutputStreamWrapper;
import de.uniluebeck.itm.wsn.drivers.core.operation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class that implements the <code>DeviceAsync</code> interface as a queue. For using this implementation an
 * <code>OperationQueue</code> is needed.
 *
 * @author Malte Legenhausen
 */
public class QueuedDeviceAsync implements DeviceAsync {

	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(QueuedDeviceAsync.class);

	/**
	 * Queue that schedules all <code>Operation</code> instances.
	 */
	private final OperationQueue queue;

	/**
	 * The <code>Device</code> that has to be executed async.
	 */
	private final Device<? extends Connection> device;

	private PipedInputStream inputStreamPipedInputStream = new PipedInputStream();

	private PipedOutputStream inputStreamPipedOutputStream = new PipedOutputStream();

	private volatile boolean deviceInputStreamAvailableForReading = true;

	private final Lock deviceInputStreamLock = new ReentrantLock();

	private final Condition deviceInputStreamDataAvailable = deviceInputStreamLock.newCondition();

	private class DeviceInputStreamToPipeCopyWorker implements Runnable {

		public volatile boolean shutdown = false;

		@Override
		public void run() {
			try {

				final InputStream inputStream = device.getConnection().getInputStream();

				while (!shutdown) {

					deviceInputStreamLock.lock();
					try {
						deviceInputStreamDataAvailable.await(100, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						LOG.error("" + e, e);
					} finally {
						deviceInputStreamLock.unlock();
					}

					if (deviceInputStreamAvailableForReading) {
						copyAvailableBytes(inputStream, inputStreamPipedOutputStream);
					}
				}

			} catch (IOException e) {
				LOG.error("IOException while reading from device InputStream: " + e, e);
			}
		}

		private void copyAvailableBytes(final InputStream inputStream, final OutputStream outputStream)
				throws IOException {

			final int bytesAvailable = inputStream.available();

			if (bytesAvailable > 0) {

				byte[] buffer = new byte[bytesAvailable];
				final int read = inputStream.read(buffer);

				outputStream.write(buffer, 0, read);
			}
		}

	}
	private final DeviceInputStreamToPipeCopyWorker deviceInputStreamToPipeCopyWorker =
			new DeviceInputStreamToPipeCopyWorker();

	/**
	 * Constructor.
	 *
	 * @param queue  The <code>OperationQueue</code> that schedules all operations.
	 * @param device The <code>Device</code> that provides all operations that can be executed.
	 */
	@Inject
	public QueuedDeviceAsync(final ExecutorService executorService, final OperationQueue queue,
							 final Device<? extends Connection> device) {

		checkNotNull(executorService);
		checkNotNull(queue);
		checkNotNull(device);
		checkNotNull(device.getConnection());

		this.queue = queue;
		this.device = device;

		try {
			this.inputStreamPipedInputStream.connect(inputStreamPipedOutputStream);
		} catch (IOException e) {
			LOG.error("" + e, e);
			throw new RuntimeException(e);
		}

		queue.addListener(new OperationQueueAdapter<Object>() {
			@Override
			public void onStateChanged(final StateChangedEvent<Object> event) {
				deviceInputStreamAvailableForReading = !isOperationRunning();
			}
		}
		);

		device.getConnection().addListener(new DataAvailableListener() {
			@Override
			public void dataAvailable(final Connection connection) {
				deviceInputStreamLock.lock();
				try {
					deviceInputStreamDataAvailable.signal();
				} finally {
					deviceInputStreamLock.unlock();
				}
			}
		}
		);

		executorService.execute(deviceInputStreamToPipeCopyWorker);
	}

	@Override
	public OperationHandle<ChipType> getChipType(long timeout, AsyncCallback<ChipType> callback) {
		LOG.debug("Reading Chip Type (Timeout: " + timeout + "ms");
		GetChipTypeOperation operation = device.createGetChipTypeOperation();
		checkNotNullOperation(operation, "The Operation getChipType is not available");
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> eraseFlash(long timeout, AsyncCallback<Void> callback) {
		LOG.debug("Erase flash (Timeout: " + timeout + "ms)");
		EraseFlashOperation operation = device.createEraseFlashOperation();
		checkNotNullOperation(operation, "The Operation eraseFlash is not avialable");
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> program(byte[] data, long timeout, AsyncCallback<Void> callback) {
		LOG.debug("Program device (timeout: " + timeout + "ms)");
		ProgramOperation operation = device.createProgramOperation();
		checkNotNullOperation(operation, "The Operation program is not available");
		operation.setBinaryImage(data);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<byte[]> readFlash(int address, int length, long timeout, AsyncCallback<byte[]> callback) {
		LOG.debug("Read flash (address: " + address + ", length: " + length + ", timeout: " + timeout + "ms)");
		ReadFlashOperation operation = device.createReadFlashOperation();
		checkNotNullOperation(operation, "The Operation readFlash is not available");
		operation.setAddress(address, length);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<MacAddress> readMac(long timeout, AsyncCallback<MacAddress> callback) {
		LOG.debug("Read mac (timeout: " + timeout + "ms)");
		ReadMacAddressOperation operation = device.createReadMacAddressOperation();
		checkNotNullOperation(operation, "The Operation readMac is not available");
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> reset(long timeout, AsyncCallback<Void> callback) {
		LOG.debug("Reset device (timeout: " + timeout + "ms)");
		ResetOperation operation = device.createResetOperation();
		checkNotNullOperation(operation, "The Operation reset is not available");
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> send(byte[] message, long timeout, AsyncCallback<Void> callback) {
		LOG.debug("Send packet to device (timeout: " + timeout + "ms)");
		SendOperation operation = device.createSendOperation();
		checkNotNullOperation(operation, "The Operation send is not available");
		operation.setMessage(message);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> writeFlash(int address,
											byte[] data,
											int length,
											long timeout,
											AsyncCallback<Void> callback) {
		LOG.debug("Write flash (address: " + address + ", length: " + length + ", timeout: " + timeout + "ms)");
		final WriteFlashOperation operation = device.createWriteFlashOperation();
		checkNotNullOperation(operation, "The Operation writeFlash is not available");
		operation.setData(address, data, length);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> writeMac(MacAddress macAddress, long timeout, AsyncCallback<Void> callback) {
		LOG.debug("Write mac (mac address: " + macAddress + ", timeout: " + timeout + "ms)");
		final WriteMacAddressOperation operation = device.createWriteMacAddressOperation();
		checkNotNullOperation(operation, "The Operation writeMac is not available");
		operation.setMacAddress(macAddress);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public InputStream getInputStream() {
		return inputStreamPipedInputStream;
	}

	@Override
	public OutputStream getOutputStream() {
		return new SendOutputStreamWrapper(this);
	}

	@Override
	public void close() throws IOException {
		deviceInputStreamToPipeCopyWorker.shutdown = true;
	}

	private void checkNotNullOperation(Operation<?> operation, String message) {
		if (operation == null) {
			throw new UnsupportedOperationException(message);
		}
	}

	private boolean isOperationRunning() {
		return Iterators.any(queue.getOperations().iterator(), new Predicate<Operation<?>>() {
			@Override
			public boolean apply(Operation<?> input) {
				return State.RUNNING.equals(input.getState());
			}
		}
		);
	}
}
