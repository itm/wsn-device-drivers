package de.uniluebeck.itm.wsn.drivers.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import de.uniluebeck.itm.tr.util.ExecutorUtils;
import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.DeviceModule;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.concurrent.OperationExecutor;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationCallback;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationCallbackAdapter;


/**
 * This class can be used for testing on physical devices. Simple set all parameters and execute the run method.
 * 
 * @author Malte Legenhausen
 */
public class GenericDeviceExample {
	
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
	private static final long DEFAULT_MAC_ADDRESS_VALUE = 1024;
	
	/**
	 * The length to read.
	 */
	private static final int READ_LENGTH = 32;
	
	/**
	 * Timeout that is used for waiting for the executors to shutdown.
	 */
	private static final int EXECUTOR_TIMEOUT = 10;
	
	/**
	 * The device async reference for this example.
	 */
	private Device device;
	
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
	
	private OutputStream outputStream;
	
	private Injector injector;
	
	public GenericDeviceExample() {
		
	}

	public void addByteReceiver(ByteReceiver receiver) {
		service.addByteReceiver(receiver);
	}
	
	public void removeByteReceiver(ByteReceiver receiver) {
		service.removeByteReceiver(receiver);
	}

	public void setImageInputStream(final InputStream anImage) {
		image = anImage;
	}

	public void setUri(final String uri) {
		this.uri = uri;
	}
	
	public void setMessage(final byte[] aMessagePacket) {
		this.messagePacket = new byte[aMessagePacket.length];
		System.arraycopy(aMessagePacket, 0, this.messagePacket, 0, aMessagePacket.length);
	}
	
	public void setModule(Module module) {
		injector = Guice.createInjector(new DeviceModule(), module);
	}

	/**
	 * Initialize the example.
	 * Should be called after all parameters has been set.
	 */
	private void init() {
		device = injector.getInstance(Device.class);
		outputStream = device.getOutputStream();
	}
	
	/**
	 * Connect the device to the given uri.
	 */
	private void connect() {
		System.out.println("Connecting to: " + uri);
		try {
			device.connect(uri);
			service.setInputStream(device.getInputStream());
			service.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		
		final OperationCallback<Void> callback = new OperationCallbackAdapter<Void>() {
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
				throwable.printStackTrace();
			}
		};
		
		final byte[] bytes = ByteStreams.toByteArray(image);
		System.out.println("Image length: " + bytes.length);
		try {
			device.program(bytes, PROGRAM_TIMEOUT, callback);
		} catch (UnsupportedOperationException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Reset the device.
	 */
	private void resetOperation() {
		final OperationCallback<Void> callback = new OperationCallbackAdapter<Void>() {
			public void onExecute() {
				System.out.println("Resetting device...");
			}
			
			@Override
			public void onSuccess(final Void result) {
				System.out.println("Device successful reset");
			}
			
			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		};
		try {
			device.reset(RESET_TIMEOUT, callback);
		} catch (UnsupportedOperationException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Read the current <code>MacAddress</code>.
	 * Write a new <code>MacAddress</code>.
	 * Read the new <code>MacAddress</code>.
	 */
	private void macAddressOperations() {		
		final OperationCallback<MacAddress> callback = new OperationCallbackAdapter<MacAddress>() {
			
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
				throwable.printStackTrace();
			}
		};
		try {
			device.readMac(READ_MAC_ADDRESS_TIMEOUT, callback);
		} catch (UnsupportedOperationException e) {
			System.err.println(e.getMessage());
		}
		
		// Write a new mac address.
		OperationCallback<Void> writeMacCallback = new OperationCallbackAdapter<Void>() {

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
				throwable.printStackTrace();
			}
		};
		try {
			device.writeMac(macAddress, WRITE_MAC_ADDRESS_TIMEOUT, writeMacCallback);
		} catch (UnsupportedOperationException e) {
			System.err.println(e.getMessage());
		}
		try {
			device.readMac(READ_MAC_ADDRESS_TIMEOUT, callback);
		} catch (UnsupportedOperationException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Read a random value from the flash.
	 */
	public void readFlashOperation() {
		final OperationCallback<byte[]> callback = new OperationCallbackAdapter<byte[]>() {
			
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
		try {
			device.readFlash(0, READ_LENGTH, RESET_TIMEOUT, callback);
		} catch (UnsupportedOperationException e) {
			System.err.println(e.getMessage());
		}
		
	}
	
	/**
	 * Read the chip type from the device.
	 */
	public void chipTypeOperation() {
		OperationCallback<ChipType> callback = new OperationCallbackAdapter<ChipType>() {

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
		};
		try {
			device.getChipType(READ_MAC_ADDRESS_TIMEOUT, callback);
		} catch (UnsupportedOperationException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Send a message to the device.
	 */
	private void sendOperation() {
		try {
			outputStream.write(messagePacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Wait until all operations has finished.
	 */
	private void waitForOperationsToFinish() {
		// Wait until the queue is empty.
		OperationExecutor queue = injector.getInstance(OperationExecutor.class);
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
			while (System.in.read() == -1) {
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
		System.out.println("Stopping InputStreamReaderService...");
		service.stopAndWait();
		System.out.println("InputStreamReaderService stopped");
		System.out.println("Closing OutputStream...");
		Closeables.closeQuietly(outputStream);
		System.out.println("OutputStream closed");
		System.out.println("Shutting down executor...");
		ExecutorUtils.shutdown(injector.getInstance(ScheduledExecutorService.class), 
				EXECUTOR_TIMEOUT, TimeUnit.SECONDS);
		System.out.println("Executor shut down");
		System.out.println("Closing connection...");
		Closeables.closeQuietly(device);
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

	public void setMacAddress(final MacAddress macAddress) {
		this.macAddress = macAddress;
	}

	public MacAddress getMacAddress() {
		return macAddress;
	}
}
