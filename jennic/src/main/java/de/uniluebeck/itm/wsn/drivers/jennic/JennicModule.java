package de.uniluebeck.itm.wsn.drivers.jennic;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.Program;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgramInterceptor;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseResetOperation;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseSerialPortConnection;

public class JennicModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(EraseFlashOperation.class).to(JennicEraseFlashOperation.class);
		bind(GetChipTypeOperation.class).to(JennicGetChipTypeOperation.class);
		bind(ProgramOperation.class).to(JennicProgramOperation.class);
		bind(ReadFlashOperation.class).to(JennicReadFlashOperation.class);
		bind(ReadMacAddressOperation.class).to(JennicReadMacAddressOperation.class);
		bind(ResetOperation.class).to(iSenseResetOperation.class);
		bind(WriteMacAddressOperation.class).to(JennicWriteMacAddressOperation.class);
		bind(WriteFlashOperation.class).to(JennicWriteFlashOperation.class);
		
		SerialPortConnection connection = new iSenseSerialPortConnection();
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Program.class), new SerialPortProgramInterceptor(connection));
		bind(SerialPortConnection.class).toInstance(connection);
		bind(Connection.class).toInstance(connection);
	}
}
