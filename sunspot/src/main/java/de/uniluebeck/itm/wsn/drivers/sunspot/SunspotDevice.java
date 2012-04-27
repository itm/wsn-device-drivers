package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationFuture;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;

import static com.google.common.base.Preconditions.checkState;

public class SunspotDevice implements Device, SunspotBaseStationListener {

	private static final Logger log = LoggerFactory.getLogger(SunspotDevice.class);

	private String macAddress;

	private SunspotBaseStation baseStation;

	private volatile boolean connected;

	private PipedOutputStream outgoingOutputStream;

	private InputStream incomingInputStream;

	private PipedInputStream outgoingInputStream;

	private OutputStream incomingOutputStream;

	private HashMap<String, String> deviceConfiguration;

	@Inject
	public SunspotDevice(SunspotBaseStation baseStation, String nodeID, PipedInputStream outgoingStream) {
		this.macAddress = nodeID;
		this.baseStation = baseStation;
		this.outgoingInputStream = outgoingStream;
		try {
			this.outgoingOutputStream = new PipedOutputStream(this.outgoingInputStream);
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	@Override
	public InputStream getInputStream() {
		return outgoingInputStream;
	}

	@Override
	public OutputStream getOutputStream() {
		return outgoingOutputStream;
	}

	@Override
	public OperationFuture<Void> send(byte[] message, long timeout, OperationListener<Void> callback) {
		checkState(connected, "Device not connected.");
		baseStation.start();
		return baseStation.send(this.macAddress, message, timeout, callback);
	}

	@Override
	public void connect(String uri) throws IOException {
		baseStation.addListener(this);
		connected = true;
		baseStation.start();
	}

	@Override
	public boolean isConnected() {
		throw (new UnsupportedOperationException());
	}


	public OperationFuture<Void> isConnected(long timeout, OperationListener<Void> callback) {
		baseStation.start();
		return baseStation.isNodeAlive(this.macAddress, timeout, callback);
	}

	@Override
	public boolean isClosed() {
		return !connected;
	}


	@Override
	public int[] getChannels() {
		return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public int waitDataAvailable(int timeout) throws TimeoutException, IOException {
		throw (new UnsupportedOperationException());
	}

	@Override
	public void clear() throws IOException {
		throw (new UnsupportedOperationException());
	}

	@Override
	public void close() throws IOException {
		baseStation.removeListener(this);
		connected = false;
	}


	public String getMacAddress() {
		return this.macAddress;
	}

	@Override
	public void messageReceived(byte[] messageBytes) {
		try {
			outgoingOutputStream.write(messageBytes);
			outgoingOutputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void setConfiguration(HashMap<String, String> deviceConfiguration) {
		this.deviceConfiguration = deviceConfiguration;
		this.macAddress = this.deviceConfiguration.get("macAddress");
	}

	public static void printInputStream(InputStream inputStream) throws IOException {

		long length = inputStream.available();
		byte[] bytes = new byte[(int) length];
		System.out.println("inputstream :" + length);
		inputStream.read(bytes);
		System.out.println("inputstrem >>" + new String(bytes));
		System.out.println("Final -----------------------");
	}

	@Override
	public OperationFuture<ChipType> getChipType(final long timeoutMillis,
												 @Nullable final OperationListener<ChipType> listener) {
		baseStation.start();
		return baseStation.getChipType(this.macAddress, timeoutMillis, listener);
	}

	@Override
	public OperationFuture<Void> program(final byte[] data, final long timeoutMillis,
										 @Nullable final OperationListener<Void> listener) {

		checkState(connected, "Device not connected.");
		baseStation.start();
		try {
			baseStation.program(this.macAddress, data, timeoutMillis, callback);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public OperationFuture<Void> eraseFlash(final long timeoutMillis,
											@Nullable final OperationListener<Void> listener) {
		throw (new UnsupportedOperationException());
	}

	@Override
	public OperationFuture<Void> writeFlash(final int address, final byte[] data, final int length,
											final long timeoutMillis,
											@Nullable final OperationListener<Void> listener) {
		throw (new UnsupportedOperationException());
	}

	@Override
	public OperationFuture<byte[]> readFlash(final int address, final int length, final long timeoutMillis,
											 @Nullable final OperationListener<byte[]> listener) {
		throw (new UnsupportedOperationException());
	}

	@Override
	public OperationFuture<MacAddress> readMac(final long timeoutMillis,
											   @Nullable final OperationListener<MacAddress> listener) {
		throw (new UnsupportedOperationException());
	}

	@Override
	public OperationFuture<Void> writeMac(final MacAddress macAddress, final long timeoutMillis,
										  @Nullable final OperationListener<Void> listener) {
		throw (new UnsupportedOperationException());
	}

	@Override
	public OperationFuture<Void> reset(final long timeoutMillis, @Nullable final OperationListener<Void> listener) {
		checkState(connected, "Device not connected.");
		baseStation.start();
		return baseStation.resetNode(this.macAddress, timeoutMillis, listener);
	}
}
