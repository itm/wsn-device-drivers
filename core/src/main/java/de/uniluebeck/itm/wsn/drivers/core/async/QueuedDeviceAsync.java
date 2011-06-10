package de.uniluebeck.itm.wsn.drivers.core.async;

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
import de.uniluebeck.itm.wsn.drivers.core.io.LockedInputStream;
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
	 * Queue that schedules all <code>Operation</code> instances.
	 */
	private final OperationQueue queue;
	
	/**
	 * The <code>Device</code> that has to be executed async.
	 */
	private final Device<? extends Connection> device;
	
	/**
	 * Lockable InputStream that allows secure access to the underlying source InputStream.
	 */
	private LockedInputStream lockedInputStream;
	
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
			this.lockedInputStream = new LockedInputStream(connection.getInputStream());
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
		LOG.debug("Reading Chip Type (Timeout: " + timeout + "ms");
		GetChipTypeOperation operation = device.createGetChipTypeOperation();
		checkNotNullOperation(operation, "The Operation getChipType is not available");
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationFuture<Void> eraseFlash(long timeout, AsyncCallback<Void> callback) {
		LOG.debug("Erase flash (Timeout: " + timeout + "ms)");
		EraseFlashOperation operation = device.createEraseFlashOperation();
		checkNotNullOperation(operation, "The Operation eraseFlash is not avialable");
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationFuture<Void> program(byte[] data, long timeout, AsyncCallback<Void> callback) {
		LOG.debug("Program device (timeout: " + timeout + "ms)");
		ProgramOperation operation = device.createProgramOperation();
		checkNotNullOperation(operation, "The Operation program is not available");
		operation.setBinaryImage(data);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationFuture<byte[]> readFlash(int address, int length, long timeout, AsyncCallback<byte[]> callback) {
		LOG.debug("Read flash (address: " + address + ", length: " + length + ", timeout: " + timeout + "ms)");
		ReadFlashOperation operation = device.createReadFlashOperation();
		checkNotNullOperation(operation, "The Operation readFlash is not available");
		operation.setAddress(address, length);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationFuture<MacAddress> readMac(long timeout, AsyncCallback<MacAddress> callback) {
		LOG.debug("Read mac (timeout: " + timeout + "ms)");
		ReadMacAddressOperation operation = device.createReadMacAddressOperation();
		checkNotNullOperation(operation, "The Operation readMac is not available");
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationFuture<Void> reset(long timeout, AsyncCallback<Void> callback) {
		LOG.debug("Reset device (timeout: " + timeout + "ms)");
		ResetOperation operation = device.createResetOperation();
		checkNotNullOperation(operation, "The Operation reset is not available");
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationFuture<Void> send(byte[] message, long timeout, AsyncCallback<Void> callback) {
		LOG.debug("Send packet to device (timeout: " + timeout + "ms)");
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
		LOG.debug("Write flash (address: " + address + ", length: " + length + ", timeout: " + timeout + "ms)");
		final WriteFlashOperation operation = device.createWriteFlashOperation();
		checkNotNullOperation(operation, "The Operation writeFlash is not available");
		operation.setData(address, data, length);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationFuture<Void> writeMac(MacAddress macAddress, long timeout, AsyncCallback<Void> callback) {
		LOG.debug("Write mac (mac address: " + macAddress + ", timeout: " + timeout + "ms)");
		final WriteMacAddressOperation operation = device.createWriteMacAddressOperation();
		checkNotNullOperation(operation, "The Operation writeMac is not available");
		operation.setMacAddress(macAddress);
		return queue.addOperation(operation, timeout, callback);
	}
	
	@Override
	public InputStream getInputStream() {
		return lockedInputStream;
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
		lockedInputStream.setLocked(locked);
	}
}
