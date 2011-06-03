package de.uniluebeck.itm.wsn.drivers.core.async;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.io.LockedInputStream;
import de.uniluebeck.itm.wsn.drivers.core.io.SendOutputStreamWrapper;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
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
	
	private LockedInputStream lockedInputStream;
	
	/**
	 * Constructor.
	 * 
	 * @param queue The <code>OperationQueue</code> that schedules all operations.
	 * @param device The <code>Device</code> that provides all operations that can be executed.
	 */
	@Inject
	public QueuedDeviceAsync(final OperationQueue queue, final Device<? extends Connection> device) {
		this.queue = queue;
		this.device = device;
		
		InputStream inputStream = device.getConnection().getInputStream();
		lockedInputStream = new LockedInputStream(inputStream);
		new LockedInputStreamManager(queue, lockedInputStream);
	}
	
	@Override
	public OperationHandle<ChipType> getChipType(final long timeout, final AsyncCallback<ChipType> callback) {
		LOG.debug("Reading Chip Type (Timeout: " + timeout + "ms");
		final GetChipTypeOperation operation = device.createGetChipTypeOperation();
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> eraseFlash(final long timeout, final AsyncCallback<Void> callback) {
		LOG.debug("Erase flash (Timeout: " + timeout + "ms)");
		final EraseFlashOperation operation = device.createEraseFlashOperation();
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> program(final byte[] data, final long timeout, final AsyncCallback<Void> callback) {
		LOG.debug("Program device (timeout: " + timeout + "ms)");
		final ProgramOperation operation = device.createProgramOperation();
		operation.setBinaryImage(data);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<byte[]> readFlash(final int address, final int length, final long timeout, final AsyncCallback<byte[]> callback) {
		LOG.debug("Read flash (address: " + address + ", length: " + length + ", timeout: " + timeout + "ms)");
		final ReadFlashOperation operation = device.createReadFlashOperation();
		operation.setAddress(address, length);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<MacAddress> readMac(final long timeout, final AsyncCallback<MacAddress> callback) {
		LOG.debug("Read mac (timeout: " + timeout + "ms)");
		final ReadMacAddressOperation operation = device.createReadMacAddressOperation();
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> reset(final long timeout, final AsyncCallback<Void> callback) {
		LOG.debug("Reset device (timeout: " + timeout + "ms)");
		final ResetOperation operation = device.createResetOperation();
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> send(final byte[] message, final long timeout, final AsyncCallback<Void> callback) {
		LOG.debug("Send packet to device (timeout: " + timeout + "ms)");
		final SendOperation operation = device.createSendOperation();
		operation.setMessage(message);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> writeFlash(final int address, final byte[] data, final int length, final long timeout, final AsyncCallback<Void> callback) {
		LOG.debug("Write flash (address: " + address + ", length: " + length + ", timeout: " + timeout + "ms)");
		final WriteFlashOperation operation = device.createWriteFlashOperation();
		operation.setData(address, data, length);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> writeMac(final MacAddress macAddress, final long timeout, final AsyncCallback<Void> callback) {
		LOG.debug("Write mac (mac address: " + macAddress + ", timeout: " + timeout + "ms)");
		final WriteMacAddressOperation operation = device.createWriteMacAddressOperation();
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
}
