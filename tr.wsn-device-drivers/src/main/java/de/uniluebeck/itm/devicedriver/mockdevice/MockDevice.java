package de.uniluebeck.itm.devicedriver.mockdevice;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.MacAddress;
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
	
	private MacAddress macAddress;
	
	private ChipType chipType;
	
	private byte[] flashRom;
	
	private int[] channels = new int[] {11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
	
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
	private TimeUnit aliveTimeUnit = TimeUnit.SECONDS;
	
	/**
	 *
	 */
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
	
	public MockDevice(int flashSize, ChipType chipType, MacAddress macAddress) {
		this.flashRom = new byte[flashSize];
		this.chipType = chipType;
		this.macAddress = macAddress;
	}
	
	@Override
	public Connection getConnection() {
		return null;
	}

	@Override
	public int[] getChannels() {
		return channels;
	}

	public void setChannels(int[] channels) {
		this.channels = channels;
	}

	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		return new MockGetChipTypeOperation(chipType);
	}

	@Override
	public ProgramOperation createProgramOperation() {
		return new MockProgramOperation(flashRom);
	}

	@Override
	public EraseFlashOperation createEraseFlashOperation() {
		return new MockEraseFlashOperation(flashRom);
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		return new MockWriteFlashOperation(flashRom);
	}

	@Override
	public ReadFlashOperation createReadFlashOperation() {
		return new MockReadFlashOperation(flashRom);
	}

	@Override
	public ReadMacAddressOperation createReadMacAddressOperation() {
		return new MockReadMacAddress(macAddress);
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		return new MockWriteMacAddressOperation(this);
	}

	@Override
	public ResetOperation createResetOperation() {
		return new MockResetOperation(this);
	}

	@Override
	public SendOperation createSendOperation() {
		return null;
	}
	
	public void setMacAddress(MacAddress macAddress) {
		this.macAddress = macAddress;
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
