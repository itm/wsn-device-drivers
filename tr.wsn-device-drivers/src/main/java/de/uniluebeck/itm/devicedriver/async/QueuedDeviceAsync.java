package de.uniluebeck.itm.devicedriver.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.MessagePlainTextListener;
import de.uniluebeck.itm.devicedriver.PacketType;
import de.uniluebeck.itm.devicedriver.operation.EraseFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.ProgramOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.devicedriver.operation.ResetOperation;
import de.uniluebeck.itm.devicedriver.operation.SendOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteMacAddressOperation;

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
	private final Device device;
	
	/**
	 * Constructor.
	 * 
	 * @param queue The <code>OperationQueue</code> that schedules all operations.
	 * @param device The <code>Device</code> that provides all operations that can be executed.
	 */
	@Inject
	public QueuedDeviceAsync(final OperationQueue queue, final Device device) {
		this.queue = queue;
		this.device = device;
	}

	@Override
	public void addListener(final MessagePacketListener listener, final PacketType... types) {
		device.addListener(listener, types);
	}

	@Override
	public void addListener(final MessagePacketListener listener, final int... types) {
		device.addListener(listener, types);
	}
	
	@Override
	public void addListener(final MessagePacketListener listener) {
		device.addListener(listener);
	}
	
	@Override
	public void addListener(final MessagePlainTextListener listener) {
		device.addListener(listener);
	}
	
	@Override
	public void removeListener(final MessagePacketListener listener) {
		device.removeListener(listener);
	}
	
	@Override
	public void removeListener(final MessagePlainTextListener listener) {
		device.removeListener(listener);
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
	public OperationHandle<Void> send(final MessagePacket packet, final long timeout, final AsyncCallback<Void> callback) {
		LOG.debug("Send packet to device (timeout: " + timeout + "ms)");
		final SendOperation operation = device.createSendOperation();
		operation.setMessagePacket(packet);
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
	
	public OperationQueue getOperationQueue() {
		return queue;
	}
}
