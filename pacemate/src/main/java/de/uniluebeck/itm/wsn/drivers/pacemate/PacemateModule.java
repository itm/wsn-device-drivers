package de.uniluebeck.itm.wsn.drivers.pacemate;

import com.google.inject.AbstractModule;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortEnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortLeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseResetOperation;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseSerialPortConnection;

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
		
		SerialPortConnection connection = new iSenseSerialPortConnection();
		bind(SerialPortConnection.class).toInstance(connection);
		bind(Connection.class).toInstance(connection);
	}
}
