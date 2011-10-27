package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.ProgrammingMode;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;

public class TelosbModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(EraseFlashOperation.class).to(TelosbEraseFlashOperation.class);
		bind(GetChipTypeOperation.class).to(TelosbGetChipTypeOperation.class);
		bind(ProgramOperation.class).to(TelosbProgramOperation.class);
		bind(ResetOperation.class).to(TelosbResetOperation.class);
		bind(WriteFlashOperation.class).to(TelosbWriteFlashOperation.class);
		
		SerialPortConnection connection = new TelosbSerialPortConnection();
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(ProgrammingMode.class), 
				new TelosbProgramInterceptor(connection, getProvider(BSLTelosb.class)));
		bind(SerialPortConnection.class).toInstance(connection);
		bind(Connection.class).toInstance(connection);
	}

}
