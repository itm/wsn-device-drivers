package de.uniluebeck.itm.devicedriver.async;

import java.io.InputStream;
import java.io.OutputStream;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.EraseFlashOperation;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.PacketType;
import de.uniluebeck.itm.devicedriver.ProgramOperation;
import de.uniluebeck.itm.devicedriver.ReadFlashOperation;
import de.uniluebeck.itm.devicedriver.ReadMacAddressOperation;
import de.uniluebeck.itm.devicedriver.ResetOperation;
import de.uniluebeck.itm.devicedriver.SendOperation;
import de.uniluebeck.itm.devicedriver.WriteFlashOperation;
import de.uniluebeck.itm.devicedriver.WriteMacAddressOperation;

public class QueuedDeviceAsync implements DeviceAsync {
	
	private final OperationQueue queue;
	
	private final Device device;
	
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
		EraseFlashOperation operation = device.createEraseFlashOperation();
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public InputStream getInputStream() {
		return device.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() {
		return device.getOutputStream();
	}

	@Override
	public OperationHandle<Void> program(byte[] binaryImage, long timeout, AsyncCallback<Void> callback) {
		ProgramOperation operation = device.createProgramOperation();
		operation.setBinaryImage(binaryImage);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<byte[]> readFlash(int address, int length, long timeout, AsyncCallback<byte[]> callback) {
		ReadFlashOperation operation = device.createReadFlashOperation();
		operation.setAddress(address, length);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<MacAddress> readMac(long timeout, AsyncCallback<MacAddress> callback) {
		ReadMacAddressOperation operation = device.createReadMacAddressOperation();
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public void removeMessagePacketListener(MessagePacketListener listener) {
		device.removeMessagePacketListener(listener);
	}

	@Override
	public OperationHandle<Void> reset(long timeout, AsyncCallback<Void> callback) {
		ResetOperation operation = device.createResetOperation();
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> send(MessagePacket packet, long timeout, AsyncCallback<Void> callback) {
		SendOperation operation = device.createSendOperation();
		operation.setMessagePacket(packet);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> writeFlash(int address, byte[] data, int length, long timeout, AsyncCallback<Void> callback) {
		WriteFlashOperation operation = device.createWriteFlashOperation();
		operation.setData(address, data, length);
		return queue.addOperation(operation, timeout, callback);
	}

	@Override
	public OperationHandle<Void> writeMac(MacAddress macAddress, long timeout, AsyncCallback<Void> callback) {
		WriteMacAddressOperation operation = device.createWriteMacAddressOperation();
		operation.setMacAddress(macAddress);
		return queue.addOperation(operation, timeout, callback);
	}
}
