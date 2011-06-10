package eu.smartsantander.wsn.drivers.waspmote;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.io.Closeables;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionEvent;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionListener;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.async.AsyncAdapter;
import de.uniluebeck.itm.wsn.drivers.core.async.AsyncCallback;
import de.uniluebeck.itm.wsn.drivers.core.async.DeviceAsync;
import de.uniluebeck.itm.wsn.drivers.core.async.OperationQueue;
import de.uniluebeck.itm.wsn.drivers.core.async.QueuedDeviceAsync;
import de.uniluebeck.itm.wsn.drivers.core.async.thread.PausableExecutorOperationQueue;
import de.uniluebeck.itm.wsn.drivers.core.io.InputStreamReaderService;
import de.uniluebeck.itm.wsn.drivers.core.io.MessagePacketListener;
import de.uniluebeck.itm.wsn.drivers.core.util.HexUtils;

/**
 * @author TLMAT UC
 */
public class WaspmoteDeviceExample implements ConnectionListener, Runnable {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		final String port = args[0];

		final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(5);
		final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3, 3, 10, TimeUnit.SECONDS, queue);

		final WaspmoteDevice device77 = new WaspmoteDevice(77, new WaspmoteVirtualSerialPortConnection());
		final WaspmoteDeviceExample example77 = new WaspmoteDeviceExample();
		example77.setDevice(device77);
		example77.setPort(port);

		final WaspmoteDevice device34 = new WaspmoteDevice(34, new WaspmoteVirtualSerialPortConnection());
		final WaspmoteDeviceExample example34 = new WaspmoteDeviceExample();
		example34.setDevice(device34);
		example34.setPort(port);

		threadPool.execute(example77);
		threadPool.execute(example34);
		threadPool.shutdown();
		threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

		System.out.println("===== THE END =====");
	}

	private static final int DEFAULT_SLEEP = 50;
	private static final int READ_MAC_ADDRESS_TIMEOUT = 100000;

	private final OperationQueue queue = new PausableExecutorOperationQueue();
	private WaspmoteDevice device = null;
	private Connection connection = null;
	private DeviceAsync deviceAsync;
	private InputStreamReaderService service = new InputStreamReaderService();
	private String port;

	public void setDevice(final WaspmoteDevice device) {
		this.device = device;
	}

	public void setPort(final String port) {
		this.port = port;
	}

	private void init() {
		connection = device.getConnection();
		connection.addListener(this);
		connection.addListener(service);
		deviceAsync = new QueuedDeviceAsync(queue, device);

		XBeeMessagePacketReceiver xbeeMessagePacketReceiver = new XBeeMessagePacketReceiver();
		xbeeMessagePacketReceiver.addListener(new MessagePacketListener() {

			@Override
			public void onMessagePacketReceived(byte[] event) {
				System.out.println(device.getNodeID() + ": Received Service Frame: " + HexUtils.byteArray2HexString(event, ' '));
			}
		});
		service.addByteReceiver(xbeeMessagePacketReceiver);
	}

	private void connect() {
		System.out.println(device.getNodeID() + ": Connecting device to: " + port);
		connection.connect(port);
	}

	/**
	 * Read the current <code>MacAddress</code>. Write a new
	 * <code>MacAddress</code>. Read the new <code>MacAddress</code>.
	 */
	private void macAddressOperations() {
		final AsyncCallback<MacAddress> callback = new AsyncAdapter<MacAddress>() {

			@Override
			public void onExecute() {
				System.out.println(device.getNodeID() + ": Reading MAC address...");
			}

			@Override
			public void onProgressChange(final float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println(device.getNodeID() + ": Reading MAC address progress: " + percent + "%");
			}

			@Override
			public void onSuccess(final MacAddress result) {
				System.out.println(device.getNodeID() + ": MAC address: " + result);
			}

			@Override
			public void onFailure(Throwable throwable) {
				System.out.println(device.getNodeID() + ": Reading MAC address progress: Failed");
				if (throwable instanceof UnsupportedOperationException) {
					System.err.println("Read mac address is not supported by this device.");
				} else {
					throwable.printStackTrace();
				}
			}
		};
		deviceAsync.readMac(READ_MAC_ADDRESS_TIMEOUT, callback);
	}

	/**
	 * Wait until all operations has finished.
	 */
	private void waitForOperationsToFinish() {
		// Wait until the queue is empty.
		while (!queue.getOperations().isEmpty()) {
			try {
				Thread.sleep(DEFAULT_SLEEP);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Wait for message packets from the device.
	 */
	private void waitForMessagePackets() {
		System.out.println(device.getNodeID() + ": Waiting for messages from the device. Press Enter to shutdown...");
		try {
			while(System.in.read() == -1) {
				Thread.sleep(DEFAULT_SLEEP);
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			service.stop();
		}
	}

	/**
	 * Shutdown the queue and the connection.
	 */
	private void shutdown() {
		System.out.println(device.getNodeID() + ": Shutting down queue...");
		queue.shutdown(false);
		System.out.println(device.getNodeID() + ": Device queue terminated");
		System.out.println(device.getNodeID() + ": Closing device connection...");
		Closeables.closeQuietly(connection);
		System.out.println(device.getNodeID() + ": Device connection closed");
	}

	/**
	 * Execute the whole test case.
	 */
	public void run() {
		init();
		connect();
		macAddressOperations();
		waitForOperationsToFinish();
		waitForMessagePackets();
		shutdown();
	}

	@Override
	public void onConnectionChange(final ConnectionEvent event) {
		if (event.isConnected()) {
			System.out.println(device.getNodeID() + ": Device connected on port: " + event.getUri());
		} else {
			System.out.println(device.getNodeID() + ": Device disconnected from port: " + event.getUri());
		}
	}

}
