package de.uniluebeck.itm.wsn.drivers.telosb;

import java.io.IOException;

import de.uniluebeck.itm.wsn.drivers.core.ConnectionEvent;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionListener;
import de.uniluebeck.itm.wsn.drivers.core.Programable;
import de.uniluebeck.itm.wsn.drivers.core.exception.FlashProgramFailedException;
import de.uniluebeck.itm.wsn.drivers.core.exception.InvalidChecksumException;
import de.uniluebeck.itm.wsn.drivers.core.exception.ReceivedIncorrectDataException;
import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import de.uniluebeck.itm.wsn.drivers.core.exception.UnexpectedResponseException;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractWriteMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.SendOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.AbstractSerialPortDevice;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortLeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortSendOperation;

public class TelosbDevice extends AbstractSerialPortDevice implements Programable, ConnectionListener {
	
	private BSLTelosb bsl;
	
	public TelosbDevice(SerialPortConnection connection) {
		super(connection);
		connection.addListener(this);
		initBSL(connection.isConnected());
	}
	
	private void initBSL(boolean connected) {
		bsl = connected ? new BSLTelosb(getConnection()) : null;
	}

	@Override
	public int[] getChannels() {
		return null;
	}

	@Override
	public EnterProgramModeOperation createEnterProgramModeOperation() {
		final EnterProgramModeOperation operation = new TelosbEnterProgramModeOperation(getConnection(), bsl);
		monitor(operation);
		return operation;
	}
	
	@Override
	public LeaveProgramModeOperation createLeaveProgramModeOperation() {
		final LeaveProgramModeOperation operation = new SerialPortLeaveProgramModeOperation(getConnection());
		monitor(operation);
		return operation;
	}
	
	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		final GetChipTypeOperation operation = new TelosbGetChipTypeOperation();
		monitor(operation);
		return operation;
	}

	@Override
	public ProgramOperation createProgramOperation() {
		final ProgramOperation operation = new TelosbProgramOperation(this);
		monitor(operation);
		return operation;
	}

	@Override
	public EraseFlashOperation createEraseFlashOperation() {
		final EraseFlashOperation operation = new TelosbEraseFlashOperation(bsl);
		monitor(operation);
		return operation;
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		final WriteFlashOperation operation = new TelosbWriteFlashOperation(this);
		monitor(operation);
		return operation;
	}

	public ReadFlashOperation createReadFlashOperation() {
		final ReadFlashOperation operation = new AbstractReadFlashOperation() {
			@Override
			public byte[] execute(ProgressManager progressManager) throws Exception {
				throw new UnsupportedOperationException("readFlash is not available");
			}
		};
		return operation;
	}

	@Override
	public ReadMacAddressOperation createReadMacAddressOperation() {
		final ReadMacAddressOperation operation = new TelosbReadMacAddressOperation();
		monitor(operation);
		return operation;
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		final WriteMacAddressOperation operation = new AbstractWriteMacAddressOperation() {
			@Override
			public Void execute(ProgressManager progressManager) throws Exception {
				throw new UnsupportedOperationException("writeMacAddress is not available");
			}
		};
		return operation;
	}

	@Override
	public ResetOperation createResetOperation() {
		final ResetOperation operation = new TelosbResetOperation(bsl);
		monitor(operation);
		return operation;
	}

	@Override
	public SendOperation createSendOperation() {
		final SendOperation operation = new SerialPortSendOperation(getConnection());
		monitor(operation);
		return operation;
	}
	
	@Override
	public void onConnectionChange(ConnectionEvent event) {
		initBSL(event.isConnected());
	}
	
	public void writeFlash(int address, byte[] bytes, int len) throws IOException, FlashProgramFailedException, TimeoutException, InvalidChecksumException, ReceivedIncorrectDataException, UnexpectedResponseException {
		bsl.sendBSLCommand(BSLTelosb.CMD_TXDATABLOCK, address, len, bytes, false);
		final byte[] reply = bsl.receiveBSLReply();
		if ((reply[0] & 0xFF) != BSLTelosb.DATA_ACK) {
			throw new FlashProgramFailedException("Failed to program flash: received no ACK");
		}
	}
}
