package de.uniluebeck.itm.wsn.drivers.jennic;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;

import com.google.inject.name.Names;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.ProgrammingMode;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgramInterceptor;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseResetOperation;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseSerialPortConnection;

import java.util.Map;

public class JennicModule extends AbstractModule {

	private final Map<String, String> configuration;

	public JennicModule(final Map<String, String> configuration) {
		this.configuration = configuration;
	}

	@Override
	protected void configure() {

		bind(new TypeLiteral<Map<String, String>>() {})
			.annotatedWith(Names.named("configuration"))
			.toInstance(configuration != null ? configuration : Maps.<String, String>newHashMap());

		bind(EraseFlashOperation.class).to(JennicEraseFlashOperation.class);
		bind(GetChipTypeOperation.class).to(JennicGetChipTypeOperation.class);
		bind(ProgramOperation.class).to(JennicProgramOperation.class);
		bind(ReadFlashOperation.class).to(JennicReadFlashOperation.class);
		bind(ReadMacAddressOperation.class).to(JennicReadMacAddressOperation.class);
		bind(ResetOperation.class).to(iSenseResetOperation.class);
		bind(WriteMacAddressOperation.class).to(JennicWriteMacAddressOperation.class);
		bind(WriteFlashOperation.class).to(JennicWriteFlashOperation.class);
		
		SerialPortConnection connection = new iSenseSerialPortConnection();
		bindInterceptor(
				Matchers.any(),
				Matchers.annotatedWith(ProgrammingMode.class),
				new SerialPortProgramInterceptor(connection)
		);

		bind(SerialPortConnection.class).toInstance(connection);
		bind(Connection.class).toInstance(connection);
	}
}
