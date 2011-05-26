package de.uniluebeck.itm.wsn.drivers.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionEvent;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionListener;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.async.AsyncAdapter;
import de.uniluebeck.itm.wsn.drivers.core.async.AsyncCallback;
import de.uniluebeck.itm.wsn.drivers.core.async.DeviceAsync;
import de.uniluebeck.itm.wsn.drivers.core.async.OperationQueue;
import de.uniluebeck.itm.wsn.drivers.core.async.QueuedDeviceAsync;
import de.uniluebeck.itm.wsn.drivers.core.async.thread.PausableExecutorOperationQueue;
import de.uniluebeck.itm.wsn.drivers.core.io.ByteReceiver;
import de.uniluebeck.itm.wsn.drivers.core.io.InputStreamReaderService;


/**
 * This class can be used for testing on physical devices. Simple set all parameters and execute the run method.
 * 
 * @author Malte Legenhausen
 */
public class GenericDeviceExample implements ConnectionListener {
	
	/**
	 * Default sleep for the thread.
	 */
	private static final int DEFAULT_SLEEP = 50;
	
	/**
	 * Timeout for the programming operation.
	 */
	private static final int PROGRAM_TIMEOUT = 600000;
	
	/**
	 * Timeout for the reset operation.
	 */
	private static final int RESET_TIMEOUT = 10000;
	
	/**
	 * Timeout for the read mac address operation.
	 */
	private static final int READ_MAC_ADDRESS_TIMEOUT = 100000;
	
	/**
	 * Timeout for writing the mac address.
	 */
	private static final int WRITE_MAC_ADDRESS_TIMEOUT = 300000;
	
	/**
	 * The default mac address value.
	 */
	private static final int DEFAULT_MAC_ADDRESS_VALUE = 1024;
	
	/**
	 * The queue used for this example.
	 */
	private final OperationQueue queue = new PausableExecutorOperationQueue();
	
	/**
	 * Default null device.
	 */
	private Device<?> device = null;
	
	/**
	 * Default null connection.
	 */
	private Connection connection = null;
	
	/**
	 * The device async reference for this example.
	 */
	private DeviceAsync deviceAsync;
	
	/**
	 * Reader for the input stream.
	 */
	private InputStreamReaderService service = new InputStreamReaderService();
	
	/**
	 * InputStream for the image file.
	 */
	private InputStream image;
	
	/**
	 * The uri to which the device is attached.
	 */
	private String uri;
	
	/**
	 * The mac address that has to be written on writeMacAddressOperation.
	 */
	private MacAddress macAddress = new MacAddress(DEFAULT_MAC_ADDRESS_VALUE);
	
	/**
	 * The example message packet that is send to the device.
	 */
	private byte[] messagePacket;
	
	public void addByteReceiver(ByteReceiver receiver) {
		service.addByteReceiver(receiver);
	}
	
	public void removeByteReceiver(ByteReceiver receiver) {
		service.removeByteReceiver(receiver);
	}

	public void setDevice(final Device<?> device) {
		this.device = device;
	}

	public void setImageInputStream(final InputStream image) {
		this.image = image;
	}

	public void setUri(final String uri) {
		this.uri = uri;
	}
	
	public void setMessage(final byte[] messagePacket) {
		this.messagePacket = new byte[messagePacket.length];
		System.arraycopy(messagePacket, 0, this.messagePacket, 0, messagePacket.length);
	}

	/**
	 * Initialize the example.
	 * Should be called after all parameters has been set.
	 */
	private void init() {
		connection = device.getConnection();
		connection.addListener(this);
		deviceAsync = new QueuedDeviceAsync(queue, device);
	}
	
	/**
	 * Connect the device to the given uri.
	 */
	private void connect() {
		System.out.println("Connecting to: " + uri);
		connection.connect(uri);
	}
	
	/**
	 * Start the programming of the device.
	 * 
	 * @throws IOException When the image can not be loaded.
	 */
	private void programImage() throws IOException {
		if (image == null) {
			System.out.println("Program skipped cause no image set.");
			return;
		}
		
		final AsyncCallback<Void> callback = new AsyncAdapter<Void>() {
			@Override
			public void onExecute() {
				System.out.println("Flashing image...");
			}
			
			@Override
			public void onProgressChange(final float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Programming progress: " + percent + "%");
			}
			
			@Override
			public void onSuccess(final Void result) {
				System.out.println("Image successfully flashed");
			}
			
			@Override
			public void onFailure(Throwable throwable) {
				if (throwable instanceof UnsupportedOperationException) {
					System.err.println("Program is not supported by this device.");
				} else {
					throwable.printStackTrace();
				}
			}
		};
		
		final byte[] bytes = ByteStreams.toByteArray(image);
		System.out.println("Image length: " + bytes.length);
	    deviceAsync.program(bytes, PROGRAM_TIMEOUT, callback);
	}
	
	/**
	 * Reset the device.
	 */
	private void resetOperation() {
		final AsyncCallback<Void> callback = new AsyncAdapter<Void>() {
			public void onExecute() {
				System.out.println("Resetting device...");
			}
			
			@Override
			public void onSuccess(final Void result) {
				System.out.println("Device successful reseted");
			}
			
			@Override
			public void onFailure(Throwable throwable) {
				if (throwable instanceof UnsupportedOperationException) {
					System.err.println("Reset is not supported by this device.");
				} else {
					throwable.printStackTrace();
				}
			}
		};
		deviceAsync.reset(RESET_TIMEOUT, callback);
	}
	
	/**
	 * Read the current <code>MacAddress</code>.
	 * Write a new <code>MacAddress</code>.
	 * Read the new <code>MacAddress</code>.
	 */
	private void macAddressOperations() {		
		final AsyncCallback<MacAddress> callback = new AsyncAdapter<MacAddress>() {
			
			@Override
			public void onExecute() {
				System.out.println("Reading mac address...");
			}
			
			@Override
			public void onProgressChange(final float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reading mac address progress: " + percent + "%");
			}
			
			@Override
			public void onSuccess(final MacAddress result) {
				System.out.println("Mac Address: " + result);
			}
			
			@Override
			public void onFailure(Throwable throwable) {
				if (throwable instanceof UnsupportedOperationException) {
					System.err.println("Read mac address is not supported by this device.");
				} else {
					throwable.printStackTrace();
				}
			}
		};
		
		deviceAsync.readMac(READ_MAC_ADDRESS_TIMEOUT, callback);
		
		// Write a new mac address.
		deviceAsync.writeMac(macAddress, WRITE_MAC_ADDRESS_TIMEOUT, new AsyncAdapter<Void>() {

			@Override
			public void onExecute() {
				System.out.println("Setting Mac Address");
			}
			
			@Override
			public void onProgressChange(final float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Writing mac address progress: " + percent + "%");
			}

			@Override
			public void onSuccess(final Void result) {
				System.out.println("Mac Address written");
			}
			
			@Override
			public void onFailure(Throwable throwable) {
				if (throwable instanceof UnsupportedOperationException) {
					System.err.println("Write mac address is not supported by this device.");
				} else {
					throwable.printStackTrace();
				}
			}
		});
		deviceAsync.readMac(READ_MAC_ADDRESS_TIMEOUT, callback);
	}
	
	/**
	 * Read a random value from the flash.
	 */
	public void readFlashOperation() {
		final AsyncCallback<byte[]> callback = new AsyncAdapter<byte[]>() {
			
			@Override
			public void onExecute() {
				System.out.println("Read flash from 0 to 32...");
			}
			
			@Override
			public void onProgressChange(final float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reading flash progress: " + percent + "%");
			}
			
			@Override
			public void onSuccess(final byte[] result) {
				System.out.println("Reading result: " + result);
			}
			
			@Override
			public void onFailure(Throwable throwable) {
				if (throwable instanceof UnsupportedOperationException) {
					System.err.println("Read flash is not supported by this device.");
				} else {
					throwable.printStackTrace();
				}
			}
		};
		deviceAsync.readFlash(0, 32, RESET_TIMEOUT, callback);
	}
	
	/**
	 * Read the chip type from the device.
	 */
	public void chipTypeOperation() {
		deviceAsync.getChipType(READ_MAC_ADDRESS_TIMEOUT, new AsyncAdapter<ChipType>() {

			@Override
			public void onExecute() {
				System.out.println("Reading ChipType from device...");
			}
			
			@Override
			public void onProgressChange(final float fraction) {
				final int percent = (int) (fraction * 100.0);
				System.out.println("Reading chip type progress: " + percent + "%");
			}
			
			@Override
			public void onSuccess(final ChipType result) {
				System.out.println("Chip Type: " + result);
			}
			
			@Override
			public void onFailure(Throwable throwable) {
				if (throwable instanceof UnsupportedOperationException) {
					System.err.println("Get chip type is not supported by this device.");
				} else {
					throwable.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Send a message to the device.
	 */
	private void sendOperation() {
		deviceAsync.send(messagePacket, RESET_TIMEOUT, new AsyncAdapter<Void>() {
			public void onExecute() {
				System.out.println("Sending message");
			}
			
			public void onSuccess(final Void result) {
				System.out.println("Message send");
			}
			
			@Override
			public void onFailure(Throwable throwable) {
				if (throwable instanceof UnsupportedOperationException) {
					System.err.println("Send is not supported by this device.");
				} else {
					throwable.printStackTrace();
				}
			}
		});
		
		System.out.println("Sending via OutputStream.");
		try {
			final OutputStream outputStream = deviceAsync.getOutputStream();
			outputStream.write(messagePacket);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		System.out.println("Waiting for messages from the device.");
		System.out.println("Press any key to shutdown...");
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
		System.out.println("Shutting down queue...");
		queue.shutdown(false);
		System.out.println("Queue terminated");
		System.out.println("Closing connection...");
		Closeables.closeQuietly(connection);
		System.out.println("Connection closed");
	}
	
	/**
	 * Execute the whole test case.
	 */
	public void run() {
		init();
		connect();
		try {
			programImage();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		macAddressOperations();
		readFlashOperation();
		chipTypeOperation();
		sendOperation();
		resetOperation();
		waitForOperationsToFinish();
		waitForMessagePackets();
		shutdown();
	}
	
	@Override
	public void onConnectionChange(final ConnectionEvent event) {
		System.out.println("Connected with port: " + event.getUri());
		if (event.isConnected()) {
			service.setInputStream(device.getInputStream());
			service.start();
		} else {
			service.stopAndWait();
		}
	}

	public void setMacAddress(final MacAddress macAddress) {
		this.macAddress = macAddress;
	}

	public MacAddress getMacAddress() {
		return macAddress;
	}
}
