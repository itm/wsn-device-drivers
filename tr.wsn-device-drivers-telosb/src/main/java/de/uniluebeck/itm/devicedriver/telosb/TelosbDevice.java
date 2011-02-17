package de.uniluebeck.itm.devicedriver.telosb;

import java.io.IOException;

import de.uniluebeck.itm.devicedriver.ConnectionEvent;
import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.Programable;
import de.uniluebeck.itm.devicedriver.exception.FlashProgramFailedException;
import de.uniluebeck.itm.devicedriver.exception.InvalidChecksumException;
import de.uniluebeck.itm.devicedriver.exception.NotImplementedException;
import de.uniluebeck.itm.devicedriver.exception.ReceivedIncorrectDataException;
import de.uniluebeck.itm.devicedriver.exception.TimeoutException;
import de.uniluebeck.itm.devicedriver.exception.UnexpectedResponseException;
import de.uniluebeck.itm.devicedriver.operation.AbstractReadFlashOperation;
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
		final EnterProgramModeOperation operation = new TelosbEnterProgramModeOperation(getConnection(), bsl);
		monitorState(operation);
		return operation;
	}
	
	@Override
	public LeaveProgramModeOperation createLeaveProgramModeOperation() {
		final LeaveProgramModeOperation operation = new SerialPortLeaveProgramModeOperation(getConnection());
		monitorState(operation);
		return operation;
	}
	
	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		final GetChipTypeOperation operation = new TelosbGetChipTypeOperation();
		monitorState(operation);
		return operation;
	}

	@Override
	public ProgramOperation createProgramOperation() {
		final ProgramOperation operation = new TelosbProgramOperation(this);
		monitorState(operation);
		return operation;
	}

	@Override
	public EraseFlashOperation createEraseFlashOperation() {
		final EraseFlashOperation operation = new TelosbEraseFlashOperation(bsl);
		monitorState(operation);
		return operation;
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		final WriteFlashOperation operation = new TelosbWriteFlashOperation(this);
		monitorState(operation);
		return operation;
	}

	public ReadFlashOperation createReadFlashOperation() {
		final ReadFlashOperation operation = new AbstractReadFlashOperation() {
			@Override
			public byte[] execute(Monitor monitor) throws Exception {
				throw new NotImplementedException("readFlash is not available");
			}
		};
		return operation;
	}

	@Override
	public ReadMacAddressOperation createReadMacAddressOperation() {
		final ReadMacAddressOperation operation = new TelosbReadMacAddressOperation();
		monitorState(operation);
		return operation;
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		final WriteMacAddressOperation operation = new AbstractWriteMacAddressOperation() {
			@Override
			public Void execute(Monitor monitor) throws Exception {
				throw new NotImplementedException("writeMacAddress is not available");
			}
		};
		return operation;
	}

	@Override
	public ResetOperation createResetOperation() {
		final ResetOperation operation = new TelosbResetOperation(bsl);
		monitorState(operation);
		return operation;
	}

	@Override
	public SendOperation createSendOperation() {
		final SendOperation operation = new SerialPortSendOperation(getConnection());
		monitorState(operation);
		return operation;
	}
	
	@Override
	public void onConnectionChange(ConnectionEvent event) {
		super.onConnectionChange(event);
		
		if (event.isConnected()) {
			bsl = new BSLTelosb(this);
		}
	}
	
	public void writeFlash(int address, byte[] bytes, int len) throws IOException, FlashProgramFailedException, TimeoutException, InvalidChecksumException, ReceivedIncorrectDataException, UnexpectedResponseException {
		// verify if block range is erased
		if (!bsl.verifyBlock(address, len, null)) {
			throw new FlashProgramFailedException("Failed to program flash: block range is not erased completely");
		}

		// execute bsl patch first(only for BSL version <=1.10)
		bsl.executeBSLPatch();

		// program block
		bsl.sendBSLCommand(BSLTelosb.CMD_TXDATABLOCK, address, len, bytes, false);

		byte[] reply = bsl.receiveBSLReply();
		if ((reply[0] & 0xFF) != BSLTelosb.DATA_ACK) {
			throw new FlashProgramFailedException("Failed to program flash: received no ACK");
		}

		// verify programmed block
		if (!bsl.verifyBlock(address, len, bytes)) {
			throw new FlashProgramFailedException("Failed to program flash: verification of written data failed");
		}
	}
}
