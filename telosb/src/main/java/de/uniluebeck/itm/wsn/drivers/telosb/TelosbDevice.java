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
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
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
		return new TelosbEnterProgramModeOperation(getConnection(), bsl);
	}
	
	@Override
	public LeaveProgramModeOperation createLeaveProgramModeOperation() {
		return new SerialPortLeaveProgramModeOperation(getConnection());
	}
	
	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		return new TelosbGetChipTypeOperation();
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
		return new TelosbWriteFlashOperation(this);
	}

	public ReadFlashOperation createReadFlashOperation() {
		return null;
	}

	@Override
	public ReadMacAddressOperation createReadMacAddressOperation() {
		return new TelosbReadMacAddressOperation();
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		return null;
	}

	@Override
	public ResetOperation createResetOperation() {
		return new TelosbResetOperation(bsl);
	}

	@Override
	public SendOperation createSendOperation() {
		return new SerialPortSendOperation(getConnection());
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
