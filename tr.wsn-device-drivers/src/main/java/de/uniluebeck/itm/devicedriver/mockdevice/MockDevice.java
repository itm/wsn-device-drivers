package de.uniluebeck.itm.devicedriver.mockdevice;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.ObserverableDevice;
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

public class MockDevice extends ObserverableDevice {
	
	private class AliveRunnable implements Runnable {
		
		private int i = 0;

		private final Long started;

		private AliveRunnable() {
			started = Calendar.getInstance().getTimeInMillis();
		}

		@Override
		public void run() {
			final Long diff = (Calendar.getInstance().getTimeInMillis() - started) / 100;
			sendLogMessage("MockDevice alive since " + diff + " seconds (update #" + (++i) + ")");
		}
	}
	
	/**
	 * Logger for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(MockDevice.class);
	
	private final MockConfiguration configuration;
	
	/**
	 *
	 */
	private Future<?> aliveRunnableFuture;
	
	/**
	 *
	 */
	private long aliveTimeout = 3;

	/**
	 *
	 */
	private TimeUnit aliveTimeUnit = TimeUnit.MILLISECONDS;
	
	/**
	 *
	 */
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
	
	public MockDevice() {
		this(new MockConfiguration());
	}
	
	public MockDevice(MockConfiguration configuration) {
		this.configuration = configuration;
		
		scheduleAliveRunnable();
	}
	
	@Override
	public Connection getConnection() {
		return null;
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
		return null;
	}
	
	public long getAliveTimeout() {
		return aliveTimeout;
	}

	public void setAliveTimeout(long aliveTimeout) {
		this.aliveTimeout = aliveTimeout;
	}

	public TimeUnit getAliveTimeUnit() {
		return aliveTimeUnit;
	}

	public void setAliveTimeUnit(TimeUnit aliveTimeUnit) {
		this.aliveTimeUnit = aliveTimeUnit;
	}

	public void scheduleAliveRunnable() {
		aliveRunnableFuture = executorService.scheduleWithFixedDelay(
			new AliveRunnable(), 
			new Random().nextInt((int) aliveTimeout), 
			aliveTimeout,
			aliveTimeUnit
		);
	}

	public void stopAliveRunnable() {
		if (aliveRunnableFuture != null && !aliveRunnableFuture.isCancelled()) {
			aliveRunnableFuture.cancel(true);
		}
	}

	public void sendLogMessage(String message) {
		byte[] msgBytes = message.getBytes();
		byte[] bytes = new byte[msgBytes.length + 2];
		bytes[0] = (byte) PacketType.LOG.getValue();
		bytes[1] = (byte) PacketType.LogType.DEBUG.getValue();
		System.arraycopy(msgBytes, 0, bytes, 2, msgBytes.length);

		MessagePacket messagePacket = MessagePacket.parse(bytes, 0, bytes.length);
		logger.debug("Emitting message packet: {}", messagePacket);

		notifyMessagePacketListener(messagePacket);

	}
}
