package de.uniluebeck.itm.devicedriver.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.DeviceBinData;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.PacketType;
import de.uniluebeck.itm.devicedriver.operation.EraseFlashOperation;
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
	private static final Logger logger = LoggerFactory.getLogger(QueuedDeviceAsync.class);
	
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
	public QueuedDeviceAsync(OperationQueue queue, Device device) {
		this.queue = queue;
		this.device = device;
	}

	@Override
	public void addMessagePacketListener(MessagePacketListener listener, PacketType... types) {
		device.addMessagePacketListener(listener, types);
	}

	@Override
	public void addMessagePacketListener(MessagePacketListener listener, int... types) {
		device.addMessagePacketListener(listener, types);
	}

	@Override
	public OperationHandle<Void> eraseFlash(long timeout, AsyncCallback<Void> callback) {
		logger.debug("Erase flash (Timeout: " + timeout + "ms)");
		EraseFlashOperation operation = device.createEraseFlashOperation();
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> program(DeviceBinData binaryImage, long timeout, AsyncCallback<Void> callback) {
		logger.debug("Program device (timeout: " + timeout + "ms)");
		ProgramOperation operation = device.createProgramOperation();
		operation.setBinaryImage(binaryImage);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<byte[]> readFlash(int address, int length, long timeout, AsyncCallback<byte[]> callback) {
		logger.debug("Read flash (address: " + address + ", length: " + length + ", timeout: " + timeout + "ms)");
		ReadFlashOperation operation = device.createReadFlashOperation();
		operation.setAddress(address, length);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<MacAddress> readMac(long timeout, AsyncCallback<MacAddress> callback) {
		logger.debug("Read mac (timeout: " + timeout + "ms)");
		ReadMacAddressOperation operation = device.createReadMacAddressOperation();
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public void removeMessagePacketListener(MessagePacketListener listener) {
		device.removeMessagePacketListener(listener);
	}

	@Override
	public OperationHandle<Void> reset(long timeout, AsyncCallback<Void> callback) {
		logger.debug("Reset device (timeout: " + timeout + "ms)");
		ResetOperation operation = device.createResetOperation();
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> send(MessagePacket packet, long timeout, AsyncCallback<Void> callback) {
		logger.debug("Send packet to device (timeout: " + timeout + "ms)");
		SendOperation operation = device.createSendOperation();
		operation.setMessagePacket(packet);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> writeFlash(int address, byte[] data, int length, long timeout, AsyncCallback<Void> callback) {
		logger.debug("Write flash (address: " + address + ", length: " + length + ", timeout: " + timeout + "ms)");
		WriteFlashOperation operation = device.createWriteFlashOperation();
		operation.setData(address, data, length);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> writeMac(MacAddress macAddress, long timeout, AsyncCallback<Void> callback) {
		logger.debug("Write mac (mac address: " + macAddress + ", timeout: " + timeout + "ms)");
		WriteMacAddressOperation operation = device.createWriteMacAddressOperation();
		operation.setMacAddress(macAddress);
		return queue.addOperation(operation, timeout, callback);
	}
	
	public OperationQueue getOperationQueue() {
		return queue;
	}
}
