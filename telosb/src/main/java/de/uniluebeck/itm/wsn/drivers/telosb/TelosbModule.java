package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.LeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortLeaveProgramModeOperation;

public class TelosbModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(EnterProgramModeOperation.class).to(TelosbEnterProgramModeOperation.class);
		bind(LeaveProgramModeOperation.class).to(SerialPortLeaveProgramModeOperation.class);
		bind(EraseFlashOperation.class).to(TelosbEraseFlashOperation.class);
		bind(GetChipTypeOperation.class).to(TelosbGetChipTypeOperation.class);
		bind(ProgramOperation.class).to(TelosbProgramOperation.class);
		bind(ResetOperation.class).to(TelosbResetOperation.class);
		bind(WriteFlashOperation.class).to(TelosbWriteFlashOperation.class);
		bind(BSLTelosb.class).in(Singleton.class);
		
		SerialPortConnection connection = new TelosbSerialPortConnection();
		bind(SerialPortConnection.class).toInstance(connection);
		bind(Connection.class).toInstance(connection);
	}

}
