package de.uniluebeck.itm.devicedriver.telosb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.Programable;
import de.uniluebeck.itm.devicedriver.operation.AbstractGetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.AbstractReadMacAddressOperation;
import de.uniluebeck.itm.devicedriver.operation.AbstractWriteMacAddressOperation;
import de.uniluebeck.itm.devicedriver.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.devicedriver.operation.EraseFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.devicedriver.operation.ProgramOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.devicedriver.operation.ResetOperation;
import de.uniluebeck.itm.devicedriver.operation.SendOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteMacAddressOperation;
import de.uniluebeck.itm.devicedriver.serialport.AbstractSerialPortDevice;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortLeaveProgramModeOperation;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortSendOperation;

public class TelosbDevice extends AbstractSerialPortDevice implements Programable {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(TelosbDevice.class);
	
	private BSLTelosb bsl;
	
	public TelosbDevice(SerialPortConnection connection) {
		super(connection);
	}

	@Override
	public int[] getChannels() {
		return null;
	}

	@Override
	public EnterProgramModeOperation createEnterProgramModeOperation() {
		return new TelosbEnterProgramModeOperation(connection, bsl);
	}
	
	@Override
	public LeaveProgramModeOperation createLeaveProgramModeOperation() {
		return new SerialPortLeaveProgramModeOperation(connection);
	}
	
	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		return new AbstractGetChipTypeOperation() {
			@Override
			public ChipType execute(Monitor monitor) throws Exception {
				return ChipType.TelosB;
			}
		};
	}

	@Override
	public ProgramOperation createProgramOperation() {
		return new TelosbProgramOperation(this);
	}

	@Override
	public EraseFlashOperation createEraseFlashOperation() {
		return new TelosbEraseFlashOperation(bsl);
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		return new TelosbWriteFlashOperation(bsl);
	}

	public ReadFlashOperation createReadFlashOperation() {
		return new TelosbReadFlashOperation(bsl);
	}

	@Override
	public ReadMacAddressOperation createReadMacAddressOperation() {
		return new AbstractReadMacAddressOperation() {
			@Override
			public MacAddress execute(Monitor monitor) throws Exception {
				log.warn("readMacAddress is not implemented.");
				return null;
			}
		};
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		return new AbstractWriteMacAddressOperation() {
			@Override
			public Void execute(Monitor monitor) throws Exception {
				log.warn("writeMacAddress is not implemented.");
				return null;
			}
		};
	}

	@Override
	public ResetOperation createResetOperation() {
		return new TelosbResetOperation(bsl);
	}

	@Override
	public SendOperation createSendOperation() {
		return new SerialPortSendOperation(connection);
	}
	
	@Override
	public void onConnectionChange(Connection connection, boolean connected) {
		super.onConnectionChange(connection, connected);
		
		if (connected) {
			bsl = new BSLTelosb(this);
		}
	}
}
