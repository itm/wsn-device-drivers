package de.uniluebeck.itm.devicedriver.telosb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.Programable;
import de.uniluebeck.itm.devicedriver.exception.NotImplementedException;
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
		final EnterProgramModeOperation operation = new TelosbEnterProgramModeOperation(connection, bsl);
		monitor.monitorState(operation);
		return operation;
	}
	
	@Override
	public LeaveProgramModeOperation createLeaveProgramModeOperation() {
		final LeaveProgramModeOperation operation = new SerialPortLeaveProgramModeOperation(connection);
		monitor.monitorState(operation);
		return operation;
	}
	
	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		final GetChipTypeOperation operation = new TelosbGetChipTypeOperation();
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public ProgramOperation createProgramOperation() {
		final ProgramOperation operation = new TelosbProgramOperation(this);
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public EraseFlashOperation createEraseFlashOperation() {
		final EraseFlashOperation operation = new TelosbEraseFlashOperation(bsl);
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		final WriteFlashOperation operation = new TelosbWriteFlashOperation(bsl);
		monitor.monitorState(operation);
		return operation;
	}

	public ReadFlashOperation createReadFlashOperation() {
		final ReadFlashOperation operation = new TelosbReadFlashOperation(bsl);
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public ReadMacAddressOperation createReadMacAddressOperation() {
		final ReadMacAddressOperation operation = new TelosbReadMacAddressOperation();
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		final WriteMacAddressOperation operation = new AbstractWriteMacAddressOperation() {
			@Override
			public Void execute(Monitor monitor) throws Exception {
				log.debug("Write mac address it not available.");
				throw new NotImplementedException("Write mac address is not available.");
			}
		};
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public ResetOperation createResetOperation() {
		final ResetOperation operation = new TelosbResetOperation(bsl);
		monitor.monitorState(operation);
		return operation;
	}

	@Override
	public SendOperation createSendOperation() {
		final SendOperation operation = new SerialPortSendOperation(connection);
		monitor.monitorState(operation);
		return operation;
	}
	
	@Override
	public void onConnectionChange(Connection connection, boolean connected) {
		super.onConnectionChange(connection, connected);
		
		if (connected) {
			bsl = new BSLTelosb(this);
		}
	}
}
