package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.util.Providers;

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
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortLeaveProgramModeOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortSendOperation;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseResetOperation;

public class TelosbModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(EnterProgramModeOperation.class).to(TelosbEnterProgramModeOperation.class);
		bind(LeaveProgramModeOperation.class).to(SerialPortLeaveProgramModeOperation.class);
		bind(EraseFlashOperation.class).to(TelosbEraseFlashOperation.class);
		bind(GetChipTypeOperation.class).to(TelosbGetChipTypeOperation.class);
		bind(ProgramOperation.class).to(TelosbProgramOperation.class);
		bind(ReadFlashOperation.class).toProvider(Providers.<ReadFlashOperation>of(null));
		bind(ReadMacAddressOperation.class).toProvider(Providers.<ReadMacAddressOperation>of(null));
		bind(ResetOperation.class).to(iSenseResetOperation.class);
		bind(SendOperation.class).to(SerialPortSendOperation.class);
		bind(WriteMacAddressOperation.class).toProvider(Providers.<WriteMacAddressOperation>of(null));
		bind(WriteFlashOperation.class).to(TelosbWriteFlashOperation.class);
		bind(BSLTelosb.class).in(Singleton.class);
		
		TelosbSerialPortConnection connection = new TelosbSerialPortConnection();
		bind(TelosbSerialPortConnection.class).toInstance(connection);
		bind(SerialPortConnection.class).toInstance(connection);
		bind(Connection.class).toInstance(connection);
	}

}
