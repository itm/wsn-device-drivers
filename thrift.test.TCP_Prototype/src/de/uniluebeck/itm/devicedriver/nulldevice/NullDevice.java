package de.uniluebeck.itm.devicedriver.nulldevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.AbstractDevice;
import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.operation.AbstractEraseFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.AbstractGetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.AbstractProgramOperation;
import de.uniluebeck.itm.devicedriver.operation.AbstractReadFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.AbstractReadMacAddressOperation;
import de.uniluebeck.itm.devicedriver.operation.AbstractResetOperation;
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

public class NullDevice extends AbstractDevice {

	/**
	 * Logger for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(NullDevice.class);
	
	@Override
	public EraseFlashOperation createEraseFlashOperation() {
		return new AbstractEraseFlashOperation() {
			@Override
			public Void execute(Monitor monitor) {
				logger.warn("Null device is used. EraseFlashOperation does nothing.");
				monitor.onProgressChange(1.0f);
				return null;
			}
		};
	}

	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		return new AbstractGetChipTypeOperation() {
			@Override
			public ChipType execute(Monitor monitor) {
				logger.warn("Null device is used. GetChipTypeOperation does nothing.");
				monitor.onProgressChange(1.0f);
				return ChipType.UNKNOWN;
			}
		};
	}

	@Override
	public ProgramOperation createProgramOperation() {
		return new AbstractProgramOperation() {
			@Override
			public Void execute(Monitor monitor) {
				logger.warn("Null device is used. ProgramOperation does nothing.");
				monitor.onProgressChange(1.0f);
				return null;
			}
		};
	}

	@Override
	public ReadFlashOperation createReadFlashOperation() {
		return new AbstractReadFlashOperation() {
			@Override
			public byte[] execute(Monitor monitor) {
				logger.warn("Null device is used. ReadFlashOperation does nothing.");
				monitor.onProgressChange(1.0f);
				return new byte[] {};
			}
		};
	}

	@Override
	public int[] getChannels() {
		return new int[] { 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };
	}

	@Override
	public Connection getConnection() {
		return null;
	}

	@Override
	public ReadMacAddressOperation createReadMacAddressOperation() {
		return new AbstractReadMacAddressOperation() {
			@Override
			public MacAddress execute(Monitor monitor) {
				logger.warn("Null device is used. ReadMacAddressOperation does nothing.");
				monitor.onProgressChange(1.0f);
				return new MacAddress();
			}
		};
	}

	@Override
	public ResetOperation createResetOperation() {
		return new AbstractResetOperation() {
			@Override
			public Void execute(Monitor monitor) {
				logger.warn("Null device is used. ResetOperation does nothing.");
				monitor.onProgressChange(1.0f);
				return null;
			}
		};
	}

	@Override
	public SendOperation createSendOperation() {
		return new AbstractSendOperation() {
			@Override
			public Void execute(Monitor monitor) {
				logger.warn("Null device is used. SendOperation does nothing.");
				monitor.onProgressChange(1.0f);
				return null;
			}
		};
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		return new AbstractWriteFlashOperation() {
			@Override
			public Void execute(Monitor monitor) {
				logger.warn("Null device is used. WriteFlashOperation does nothing.");
				monitor.onProgressChange(1.0f);
				return null;
			}
		};
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		return new AbstractWriteMacAddressOperation() {
			@Override
			public Void execute(Monitor monitor) {
				logger.warn("Null device is used. WriteMacAddressOperation does nothing.");
				monitor.onProgressChange(1.0f);
				return null;
			}
		};
	}
}
