package de.uniluebeck.itm.wsn.drivers.core.nulldevice;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.wsn.drivers.core.ChipType;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractSendOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractWriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractWriteMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.SendOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteMacAddressOperation;


/**
 * Null Device that makes nothing when calling a operation.
 * 
 * @author Malte Legenhausen
 */
public class NullDevice implements Device<Connection> {

	/**
	 * Null implementation for <code>EraseFlashOperation</code>.
	 * 
	 * @author Malte Legenhausen
	 */
	private class NullEraseFlashOperation extends AbstractOperation<Void> implements EraseFlashOperation {
		@Override
		public Void execute(final ProgressManager progressManager) {
			LOG.warn("Null device is used. EraseFlashOperation does nothing.");
			return null;
		}
	}
	
	/**
	 * Null implementation for <code>GetChipTypeOperation</code>.
	 * 
	 * @author Malte Legenhausen
	 */
	private class NullGetChipTypeOperation extends AbstractOperation<ChipType> implements GetChipTypeOperation {
		@Override
		public ChipType execute(final ProgressManager progressManager) {
			LOG.warn("Null device is used. GetChipTypeOperation does nothing.");
			return ChipType.UNKNOWN;
		}
	}
	
	/**
	 * Null implementation for <code>ReadMacAddressOperation</code>.
	 * 
	 * @author Malte Legenhausen
	 */
	private class NullReadMacAddressOperation extends AbstractOperation<MacAddress> implements ReadMacAddressOperation {
		@Override
		public MacAddress execute(final ProgressManager progressManager) {
			LOG.warn("Null device is used. ReadMacAddressOperation does nothing.");
			return new MacAddress();
		}
	}
	
	/**
	 * Null implementation for <code>ResetOperation</code>.
	 * 
	 * @author Malte Legenhausen
	 */
	private class NullResetOperation extends AbstractOperation<Void> implements ResetOperation {
		@Override
		public Void execute(final ProgressManager progressManager) {
			LOG.warn("Null device is used. ResetOperation does nothing.");
			return null;
		}
	}
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(NullDevice.class);
	
	/**
	 * All possible channels for a device.
	 */
	private static final int[] CHANNELS = new int[] { 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };
	
	/**
	 * Null connection instance for this device.
	 */
	private final Connection connection = new NullConnection();
	
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
			public Void execute(final ProgressManager progressManager) {
				LOG.warn("Null device is used. ProgramOperation does nothing.");
				return null;
			}
		};
	}

	@Override
	public ReadFlashOperation createReadFlashOperation() {
		return new AbstractReadFlashOperation() {
			@Override
			public byte[] execute(final ProgressManager progressManager) {
				LOG.warn("Null device is used. ReadFlashOperation does nothing.");
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
		return connection;
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
			public Void execute(final ProgressManager progressManager) {
				LOG.warn("Null device is used. SendOperation does nothing.");
				return null;
			}
		};
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		return new AbstractWriteFlashOperation() {
			@Override
			public Void execute(final ProgressManager progressManager) {
				LOG.warn("Null device is used. WriteFlashOperation does nothing.");
				return null;
			}
		};
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		return new AbstractWriteMacAddressOperation() {
			@Override
			public Void execute(final ProgressManager progressManager) {
				LOG.warn("Null device is used. WriteMacAddressOperation does nothing.");
				return null;
			}
		};
	}

	@Override
	public InputStream getInputStream() {
		return null;
	}
}
