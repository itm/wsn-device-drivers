package de.uniluebeck.itm.devicedriver.nulldevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.ObserverableDevice;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.AbstractProgramOperation;
import de.uniluebeck.itm.devicedriver.operation.AbstractReadFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.AbstractSendOperation;
import de.uniluebeck.itm.devicedriver.operation.AbstractWriteFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.AbstractWriteMacAddressOperation;
import de.uniluebeck.itm.devicedriver.operation.EraseFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.ProgramOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.devicedriver.operation.ResetOperation;
import de.uniluebeck.itm.devicedriver.operation.SendOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteMacAddressOperation;

public class NullDevice extends ObserverableDevice {

	private class NullEraseFlashOperation extends AbstractOperation<Void> implements EraseFlashOperation {
		@Override
		public Void execute(final Monitor monitor) {
			LOG.warn("Null device is used. EraseFlashOperation does nothing.");
			monitor.onProgressChange(1.0f);
			return null;
		}
	}
	
	private class NullGetChipTypeOperation extends AbstractOperation<ChipType> implements GetChipTypeOperation {
		@Override
		public ChipType execute(final Monitor monitor) {
			LOG.warn("Null device is used. GetChipTypeOperation does nothing.");
			monitor.onProgressChange(1.0f);
			return ChipType.UNKNOWN;
		}
	}
	
	private class NullReadMacAddressOperation extends AbstractOperation<MacAddress> implements ReadMacAddressOperation {
		@Override
		public MacAddress execute(final Monitor monitor) {
			LOG.warn("Null device is used. ReadMacAddressOperation does nothing.");
			monitor.onProgressChange(1.0f);
			return new MacAddress();
		}
	}
	
	private class NullResetOperation extends AbstractOperation<Void> implements ResetOperation {
		@Override
		public Void execute(final Monitor monitor) {
			LOG.warn("Null device is used. ResetOperation does nothing.");
			monitor.onProgressChange(1.0f);
			return null;
		}
	}
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(NullDevice.class);
	
	private static final int[] CHANNELS = new int[] { 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };
	
	@Override
	public EraseFlashOperation createEraseFlashOperation() {
		return new NullEraseFlashOperation();
	}

	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		return new NullGetChipTypeOperation();
	}

	@Override
	public ProgramOperation createProgramOperation() {
		return new AbstractProgramOperation() {
			@Override
			public Void execute(final Monitor monitor) {
				LOG.warn("Null device is used. ProgramOperation does nothing.");
				monitor.onProgressChange(1.0f);
				return null;
			}
		};
	}

	@Override
	public ReadFlashOperation createReadFlashOperation() {
		return new AbstractReadFlashOperation() {
			@Override
			public byte[] execute(final Monitor monitor) {
				LOG.warn("Null device is used. ReadFlashOperation does nothing.");
				monitor.onProgressChange(1.0f);
				return new byte[] {};
			}
		};
	}

	@Override
	public int[] getChannels() {
		return CHANNELS;
	}

	@Override
	public Connection getConnection() {
		return null;
	}

	@Override
	public ReadMacAddressOperation createReadMacAddressOperation() {
		return new NullReadMacAddressOperation();
	}

	@Override
	public ResetOperation createResetOperation() {
		return new NullResetOperation();
	}

	@Override
	public SendOperation createSendOperation() {
		return new AbstractSendOperation() {
			@Override
			public Void execute(final Monitor monitor) {
				LOG.warn("Null device is used. SendOperation does nothing.");
				monitor.onProgressChange(1.0f);
				return null;
			}
		};
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		return new AbstractWriteFlashOperation() {
			@Override
			public Void execute(final Monitor monitor) {
				LOG.warn("Null device is used. WriteFlashOperation does nothing.");
				monitor.onProgressChange(1.0f);
				return null;
			}
		};
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		return new AbstractWriteMacAddressOperation() {
			@Override
			public Void execute(final Monitor monitor) {
				LOG.warn("Null device is used. WriteMacAddressOperation does nothing.");
				monitor.onProgressChange(1.0f);
				return null;
			}
		};
	}
}
