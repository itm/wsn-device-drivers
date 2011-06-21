package de.uniluebeck.itm.wsn.drivers.pacemate;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
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
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortEnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortLeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortSendOperation;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseResetOperation;

public class PacemateModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(EnterProgramModeOperation.class).to(SerialPortEnterProgramModeOperation.class);
		bind(LeaveProgramModeOperation.class).to(SerialPortLeaveProgramModeOperation.class);
		bind(EraseFlashOperation.class).to(PacemateEraseFlashOperation.class);
		bind(GetChipTypeOperation.class).to(PacemateGetChipTypeOperation.class);
		bind(ProgramOperation.class).to(PacemateProgramOperation.class);
		bind(ReadFlashOperation.class).to(PacemateReadFlashOperation.class);
		bind(ReadMacAddressOperation.class).to(PacemateReadMacAddressOperation.class);
		bind(ResetOperation.class).to(iSenseResetOperation.class);
		bind(SendOperation.class).to(SerialPortSendOperation.class);
		bind(WriteMacAddressOperation.class).toProvider(new Provider<WriteMacAddressOperation>() {
			@Override
			public WriteMacAddressOperation get() {
				return null;
			}
		});
		bind(WriteFlashOperation.class).toProvider(new Provider<WriteFlashOperation>() {
			@Override
			public WriteFlashOperation get() {
				return null;
			}
		});
		
		PacemateSerialPortConnection connection = new PacemateSerialPortConnection();
		bind(PacemateSerialPortConnection.class).toInstance(connection);
		bind(SerialPortConnection.class).toInstance(connection);
		bind(Connection.class).toInstance(connection);
	}

}
