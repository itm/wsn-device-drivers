package de.uniluebeck.itm.wsn.drivers.mock;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionEvent;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionListener;
import de.uniluebeck.itm.wsn.drivers.core.Device;
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
 * Mock device that can be used for testing.
 * 
 * @author Malte Legenhausen
 */
public class MockDevice implements Device<Connection>, ConnectionListener {
	
	/**
	 * Internal runnable that send continuesly messages.
	 * 
	 * @author Malte Legenhausen
	 */
	private class AliveRunnable implements Runnable {
		
		/**
		 * Constant to convert milliseconds to seconds.
		 */
		private static final int MILL = 1000;
		
		/**
		 * Message count.
		 */
		private int i = 0;

		/**
		 * Start time of this runnable.
		 */
		private final Long started = Calendar.getInstance().getTimeInMillis();
		
		/**
		 * The mock device for sending the device.
		 */
		private final MockDevice device;
		
		/**
		 * Constructor.
		 * 
		 * @param device A MockDevice instance.
		 */
		public AliveRunnable(final MockDevice device) {
			this.device = device;
		}

		@Override
		public void run() {
			final Long diff = (Calendar.getInstance().getTimeInMillis() - started) / MILL;
			final String message = "MockDevice alive since " + diff + " seconds (update #" + (++i) + ")";
			device.sendMessage(message);
		}
	}
	
	/**
	 * The configuration of this mock device.
	 */
	private final MockConfiguration configuration;
	
	/**
	 * The connection for this device.
	 */
	private final Connection connection;
	
	/**
	 * Executor for periodically execute a given runnable.
	 */
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
	
	/**
	 * Future of the executed runnable.
	 */
	private Future<?> aliveRunnableFuture;
	
	/**
	 * The frequency with which a message has to be send.
	 */
	private final long aliveTimeout = 1;

	/**
	 * The frequency time units. 
	 */
	private final TimeUnit aliveTimeUnit = TimeUnit.SECONDS;
	
	/**
	 * Constructor.
	 * 
	 * @param connection The connection for this device.
	 */
	public MockDevice(final Connection connection) {
		this(new MockConfiguration(), connection);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param configuration The connection for this device.
	 * @param connection An alternative configuration for the mock device.
	 */
	public MockDevice(final MockConfiguration configuration, final Connection connection) {
		this.configuration = configuration;
		this.connection = connection;
		
		connection.addListener(this);
	}
	
	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public int[] getChannels() {
		return configuration.getChannels();
	}

	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		return new MockGetChipTypeOperation(configuration);
	}

	@Override
	public ProgramOperation createProgramOperation() {
		return new MockProgramOperation(configuration);
	}

	@Override
	public EraseFlashOperation createEraseFlashOperation() {
		return new MockEraseFlashOperation(configuration);
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		return new MockWriteFlashOperation(configuration);
	}

	@Override
	public ReadFlashOperation createReadFlashOperation() {
		return new MockReadFlashOperation(configuration);
	}

	@Override
	public ReadMacAddressOperation createReadMacAddressOperation() {
		return new MockReadMacAddress(configuration);
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		return new MockWriteMacAddressOperation(configuration);
	}

	@Override
	public ResetOperation createResetOperation() {
		return new MockResetOperation(this);
	}

	@Override
	public SendOperation createSendOperation() {
		return new MockSendOperation(this);
	}
	
	/**
	 * Start the process for sending periodically messages.
	 */
	public void scheduleAliveRunnable() {
		aliveRunnableFuture = executorService.scheduleWithFixedDelay(
			new AliveRunnable(this), 
			new Random().nextInt((int) aliveTimeout), 
			aliveTimeout,
			aliveTimeUnit
		);
	}

	/**
	 * Stop the periodically sending of messages.
	 */
	public void stopAliveRunnable() {
		if (aliveRunnableFuture != null && !aliveRunnableFuture.isCancelled()) {
			aliveRunnableFuture.cancel(true);
		}
	}
	
	/**
	 * Writing a message to the output stream.
	 * 
	 * @param message The message as string.
	 */
	public void sendMessage(final String message) {
		sendMessage(message.getBytes());		
	}
	
	/**
	 * Writing a message to the output stream.
	 * 
	 * @param message The message as byte array.
	 */
	public void sendMessage(final byte[] message) {
		final OutputStream outputStream = connection.getOutputStream();
		try {
			outputStream.write(message);
			outputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onConnectionChange(ConnectionEvent event) {
		if (event.isConnected()) {
			scheduleAliveRunnable();
		} else {
			scheduleAliveRunnable();
			executorService.shutdown();
		}
	}
}
