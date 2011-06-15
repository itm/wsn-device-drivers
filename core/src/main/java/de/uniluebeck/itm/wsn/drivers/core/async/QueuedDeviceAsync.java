package de.uniluebeck.itm.wsn.drivers.core.async;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.State;
import de.uniluebeck.itm.wsn.drivers.core.event.StateChangedEvent;
import de.uniluebeck.itm.wsn.drivers.core.io.SendOutputStreamWrapper;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.SendOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteMacAddressOperation;

/**
 * Class that implements the <code>DeviceAsync</code> interface as a queue.
 * For using this implementation an <code>OperationQueue</code> is needed.
 * 
 * @author Malte Legenhausen
 */
public class QueuedDeviceAsync implements DeviceAsync {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(QueuedDeviceAsync.class);
	
	/**
	 * Message for the exception that is thrown when a negative timeout was given.
	 */
	private static final String NEGATIVE_TIMEOUT_MESSAGE = "Negative timeout is not allowed.";
	
	/**
	 * Message for the exception that is thrown when a negative address was given.
	 */
	private static final String NEGATIVE_ADDRESS_MESSAGE = "Negative address is not allowed.";
	
	/**
	 * Message for the exception that is thrown when a negative length was given.
	 */
	private static final String NEGATIVE_LENGTH_MESSAGE = "Negative length is not allowed.";
	
	/**
	 * Queue that schedules all <code>Operation</code> instances.
	 */
	private final OperationQueue queue;
	
	/**
	 * The <code>Device</code> that has to be executed async.
	 */
	private final Device<? extends Connection> device;
	
	/**
	 * Constructor.
	 * 
	 * @param queue The <code>OperationQueue</code> that schedules all operations.
	 * @param device The <code>Device</code> that provides all operations that can be executed.
	 */
	@Inject
	public QueuedDeviceAsync(OperationQueue queue, Device<? extends Connection> device) {
		this.queue = queue;
		this.device = device;
		
		final Connection connection = device.getConnection();
		if (connection != null) {
			queue.addListener(new OperationQueueAdapter<Object>() {
				@Override
				public void onStateChanged(final StateChangedEvent<Object> event) {
					lockInputStreamIfAnyRunning();
				}
			});
		}
	}
	
	@Override
	public OperationFuture<ChipType> getChipType(long timeout, AsyncCallback<ChipType> callback) {
		LOG.trace("Reading Chip Type (Timeout: " + timeout + "ms)");
		checkArgument(timeout >= 0, NEGATIVE_TIMEOUT_MESSAGE);
		GetChipTypeOperation operation = device.createGetChipTypeOperation();
		checkNotNullOperation(operation, "The Operation getChipType is not available");
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationFuture<Void> eraseFlash(long timeout, AsyncCallback<Void> callback) {
		LOG.trace("Erase flash (Timeout: " + timeout + "ms)");
		checkArgument(timeout >= 0, NEGATIVE_TIMEOUT_MESSAGE);
		EraseFlashOperation operation = device.createEraseFlashOperation();
		checkNotNullOperation(operation, "The Operation eraseFlash is not avialable");
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationFuture<Void> program(byte[] data, long timeout, AsyncCallback<Void> callback) {
		LOG.trace("Program device (timeout: " + timeout + "ms)");
		checkArgument(timeout >= 0, NEGATIVE_TIMEOUT_MESSAGE);
		ProgramOperation operation = device.createProgramOperation();
		checkNotNullOperation(operation, "The Operation program is not available");
		operation.setBinaryImage(data);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationFuture<byte[]> readFlash(int address, int length, long timeout, AsyncCallback<byte[]> callback) {
		LOG.trace("Read flash (address: " + address + ", length: " + length + ", timeout: " + timeout + "ms)");
		checkArgument(address >= 0, NEGATIVE_LENGTH_MESSAGE);
		checkArgument(length >= 0, NEGATIVE_ADDRESS_MESSAGE);
		checkArgument(timeout >= 0, NEGATIVE_TIMEOUT_MESSAGE);
		ReadFlashOperation operation = device.createReadFlashOperation();
		checkNotNullOperation(operation, "The Operation readFlash is not available");
		operation.setAddress(address, length);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationFuture<MacAddress> readMac(long timeout, AsyncCallback<MacAddress> callback) {
		LOG.trace("Read mac (timeout: " + timeout + "ms)");
		checkArgument(timeout >= 0, NEGATIVE_TIMEOUT_MESSAGE);
		ReadMacAddressOperation operation = device.createReadMacAddressOperation();
		checkNotNullOperation(operation, "The Operation readMac is not available");
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationFuture<Void> reset(long timeout, AsyncCallback<Void> callback) {
		LOG.trace("Reset device (timeout: " + timeout + "ms)");
		checkArgument(timeout >= 0, NEGATIVE_TIMEOUT_MESSAGE);
		ResetOperation operation = device.createResetOperation();
		checkNotNullOperation(operation, "The Operation reset is not available");
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationFuture<Void> send(byte[] message, long timeout, AsyncCallback<Void> callback) {
		LOG.trace("Send packet to device (timeout: " + timeout + "ms)");
		checkArgument(timeout >= 0, NEGATIVE_TIMEOUT_MESSAGE);
		SendOperation operation = device.createSendOperation();
		checkNotNullOperation(operation, "The Operation send is not available");
		operation.setMessage(message);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationFuture<Void> writeFlash(int address, 
											byte[] data, 
											int length, 
											long timeout, 
											AsyncCallback<Void> callback) {
		LOG.trace("Write flash (address: " + address + ", length: " + length + ", timeout: " + timeout + "ms)");
		checkArgument(address >= 0, NEGATIVE_LENGTH_MESSAGE);
		checkNotNull(data, "Null data is not allowed.");
		checkArgument(length >= 0, NEGATIVE_ADDRESS_MESSAGE);
		checkArgument(timeout >= 0, NEGATIVE_TIMEOUT_MESSAGE);
		WriteFlashOperation operation = device.createWriteFlashOperation();
		checkNotNullOperation(operation, "The Operation writeFlash is not available");
		operation.setData(address, data, length);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationFuture<Void> writeMac(MacAddress macAddress, long timeout, AsyncCallback<Void> callback) {
		LOG.trace("Write mac (mac address: " + macAddress + ", timeout: " + timeout + "ms)");
		checkNotNull(macAddress, "Null macAdress is not allowed.");
		checkArgument(timeout >= 0, NEGATIVE_TIMEOUT_MESSAGE);
		WriteMacAddressOperation operation = device.createWriteMacAddressOperation();
		checkNotNullOperation(operation, "The Operation writeMac is not available");
		operation.setMacAddress(macAddress);
		return queue.addOperation(operation, timeout, callback);
	}
	
	@Override
	public InputStream getInputStream() {
		return device.getConnection().getSaveInputStream();
	}
	
	@Override
	public OutputStream getOutputStream() {
		return new SendOutputStreamWrapper(this);
	}
	
	/**
	 * Getter for the queue that is used by this class.
	 * 
	 * @return The <code>OperationQueue</code> instance.
	 */
	public OperationQueue getOperationQueue() {
		return queue;
	}
	
	private void checkNotNullOperation(Operation<?> operation, String message) {
		if (operation == null) {
			throw new UnsupportedOperationException(message);
		}
	}

	private void lockInputStreamIfAnyRunning() {
		// Preventing the operation to die not deterministically in the queue, without any exception.
		List<Operation<?>> operations = new ArrayList<Operation<?>>(queue.getOperations());
		boolean locked = Iterators.any(operations.iterator(), new Predicate<Operation<?>>() {
			@Override
			public boolean apply(Operation<?> input) {
				return State.RUNNING.equals(input.getState());
			}
		});
		device.getConnection().setOperationRunning(locked);
	}
}
