package de.uniluebeck.itm.wsn.drivers.core;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.uniluebeck.itm.wsn.drivers.core.concurrent.OperationExecutor;
import de.uniluebeck.itm.wsn.drivers.core.concurrent.OperationFuture;
import de.uniluebeck.itm.wsn.drivers.core.operation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Facade for calling operation async on the device.
 *
 * @author Malte Legenhausen
 */
@Singleton
public class AbstractDevice implements Device {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(AbstractDevice.class);

	/**
	 * Pattern used for operations that are not available.
	 */
	private static final String NOT_AVAILABLE_PATTERN = "%s is not available.";

	/**
	 * Message for the exception that is thrown when a negative address was given.
	 */
	private static final String NEGATIVE_ADDRESS_MESSAGE = "Negative address is not allowed.";

	/**
	 * Message for the exception that is thrown when a negative length was given.
	 */
	private static final String NEGATIVE_LENGTH_MESSAGE = "Negative length is not allowed.";

	/**
	 * Queue that schedules all <code>OperationRunnable</code> instances.
	 */
	private final OperationExecutor executor;

	private final Connection connection;

	private final InputStream inputStream;

	private final Provider<OutputStream> outputStreamProvider;

	private final Provider<SendOperation> sendProvider;

	private Provider<GetChipTypeOperation> getChipTypeProvider;

	private Provider<EraseFlashOperation> eraseFlashProvider;

	private Provider<ProgramOperation> programProvider;

	private Provider<ReadFlashOperation> readFlashProvider;

	private Provider<ReadMacAddressOperation> readMacAddressProvider;

	private Provider<ResetOperation> resetProvider;

	private Provider<WriteFlashOperation> writeFlashProvider;

	private Provider<WriteMacAddressOperation> writeMacAddressProvider;

	@Inject
	public AbstractDevice(OperationExecutor executor, Connection connection, InputStream inputStream,
						  Provider<OutputStream> outputStreamProvider, Provider<SendOperation> sendProvider) {
		this.connection = connection;
		this.executor = executor;
		this.inputStream = inputStream;
		this.outputStreamProvider = outputStreamProvider;
		this.sendProvider = sendProvider;
	}

	@Override
	public OperationFuture<ChipType> getChipType(long timeout, @Nullable OperationCallback<ChipType> callback) {
		log.trace("Reading Chip Type (timeout: " + timeout + "ms)");
		GetChipTypeOperation operation = createOperation(getChipTypeProvider, "getChipType");
		return executor.submitOperation(operation);
	}

	@Override
	public OperationFuture<Void> eraseFlash(long timeout, @Nullable OperationCallback<Void> callback) {
		log.trace("Erase flash (timeout: " + timeout + "ms)");
		EraseFlashOperation operation = createOperation(eraseFlashProvider, "eraseFlash");
		return executor.submitOperation(operation);
	}

	@Override
	public OperationFuture<Void> program(byte[] data, long timeout, @Nullable OperationCallback<Void> callback) {
		log.trace("Program device (timeout: " + timeout + "ms)");
		ProgramOperation operation = createOperation(programProvider, "program");
		operation.setBinaryImage(data);
		return executor.submitOperation(operation);
	}

	@Override
	public OperationFuture<byte[]> readFlash(int address, int length, long timeout,
											 @Nullable OperationCallback<byte[]> callback) {
		log.trace("Read flash (address: " + address + ", length: " + length + ", timeout: " + timeout + "ms)");
		ReadFlashOperation operation = createOperation(readFlashProvider, "readFlash");
		checkArgument(address >= 0, NEGATIVE_LENGTH_MESSAGE);
		checkArgument(length >= 0, NEGATIVE_ADDRESS_MESSAGE);
		operation.setAddress(address, length);
		return executor.submitOperation(operation);
	}

	@Override
	public OperationFuture<MacAddress> readMac(long timeout, @Nullable OperationCallback<MacAddress> callback) {
		log.trace("Read mac (timeout: " + timeout + "ms)");
		ReadMacAddressOperation operation = createOperation(readMacAddressProvider, "readMac");
		return executor.submitOperation(operation);
	}

	@Override
	public OperationFuture<Void> reset(long timeout, @Nullable OperationCallback<Void> callback) {
		log.trace("Reset device (timeout: " + timeout + "ms)");
		ResetOperation operation = createOperation(resetProvider, "reset is not available");
		return executor.submitOperation(operation);
	}

	@Override
	public OperationFuture<Void> send(byte[] message, long timeout, @Nullable OperationCallback<Void> callback) {
		log.trace("Send packet to device (timeout: " + timeout + "ms)");
		SendOperation operation = createOperation(sendProvider, "send is not available");
		operation.setMessage(message);
		return executor.submitOperation(operation);
	}

	@Override
	public OperationFuture<Void> writeFlash(int address, byte[] data, int length, long timeout,
											@Nullable OperationCallback<Void> callback) {
		log.trace("Write flash (address: " + address + ", length: " + length + ", timeout: " + timeout + "ms)");
		WriteFlashOperation operation = createOperation(writeFlashProvider, "writeFlash");
		checkArgument(address >= 0, NEGATIVE_LENGTH_MESSAGE);
		checkNotNull(data, "Null data is not allowed.");
		checkArgument(length >= 0, NEGATIVE_ADDRESS_MESSAGE);
		operation.setData(address, data, length);
		return executor.submitOperation(operation);
	}

	@Override
	public OperationFuture<Void> writeMac(MacAddress macAddress, long timeout,
										  @Nullable OperationCallback<Void> callback) {
		log.trace("Write mac (mac address: " + macAddress + ", timeout: " + timeout + "ms)");
		WriteMacAddressOperation operation = createOperation(writeMacAddressProvider, "writeMac");
		checkNotNull(macAddress, "Null MAC address is not allowed.");
		operation.setMacAddress(macAddress);
		return executor.submitOperation(operation);
	}

	@Override
	public InputStream getInputStream() {
		return inputStream;
	}

	@Override
	public OutputStream getOutputStream() {
		return outputStreamProvider.get();
	}

	@Override
	public void close() throws IOException {
		connection.close();
	}

	@Override
	public void connect(String uri) throws IOException {
		connection.connect(uri);
	}

	@Override
	public boolean isConnected() {
		return connection.isConnected();
	}

	@Override
	public boolean isClosed() {
		return connection.isClosed();
	}

	@Inject(optional = true)
	public void setGetChipTypeProvider(Provider<GetChipTypeOperation> getChipTypeProvider) {
		this.getChipTypeProvider = getChipTypeProvider;
	}

	@Inject(optional = true)
	public void setEraseFlashProvider(
			Provider<EraseFlashOperation> eraseFlashProvider) {
		this.eraseFlashProvider = eraseFlashProvider;
	}

	@Inject(optional = true)
	public void setProgramProvider(Provider<ProgramOperation> programProvider) {
		this.programProvider = programProvider;
	}

	@Inject(optional = true)
	public void setReadFlashProvider(Provider<ReadFlashOperation> readFlashProvider) {
		this.readFlashProvider = readFlashProvider;
	}

	@Inject(optional = true)
	public void setReadMacAddressProvider(Provider<ReadMacAddressOperation> readMacAddressProvider) {
		this.readMacAddressProvider = readMacAddressProvider;
	}

	@Inject(optional = true)
	public void setResetProvider(Provider<ResetOperation> resetProvider) {
		this.resetProvider = resetProvider;
	}

	@Inject(optional = true)
	public void setWriteFlashProvider(Provider<WriteFlashOperation> writeFlashProvider) {
		this.writeFlashProvider = writeFlashProvider;
	}

	@Inject(optional = true)
	public void setWriteMacAddressProvider(Provider<WriteMacAddressOperation> writeMacAddressProvider) {
		this.writeMacAddressProvider = writeMacAddressProvider;
	}

	private static <T, O extends Operation<T>> O createOperation(Provider<O> provider, String operationName) {
		if (provider == null) {
			throw new UnsupportedOperationException(String.format(NOT_AVAILABLE_PATTERN, operationName));
		}
		O runnable = provider.get();
		if (runnable == null) {
			throw new UnsupportedOperationException(String.format(NOT_AVAILABLE_PATTERN, operationName));
		}
		return runnable;
	}
}
