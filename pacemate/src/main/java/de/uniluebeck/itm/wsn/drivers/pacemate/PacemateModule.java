package de.uniluebeck.itm.wsn.drivers.pacemate;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.operation.*;
import de.uniluebeck.itm.wsn.drivers.core.serialport.ProgrammingMode;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseResetOperation;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseSerialPortConnection;

import java.util.Map;

public class PacemateModule extends AbstractModule {

	private final Map<String, String> configuration;

	public PacemateModule(final Map<String, String> configuration) {
		this.configuration = configuration;
	}

	@Override
	protected void configure() {

		bind(new TypeLiteral<Map<String, String>>() {})
			.annotatedWith(Names.named("configuration"))
			.toInstance(configuration != null ? configuration : Maps.<String, String>newHashMap());

		bind(EraseFlashOperation.class).to(PacemateEraseFlashOperation.class);
		bind(GetChipTypeOperation.class).to(PacemateGetChipTypeOperation.class);
		bind(ProgramOperation.class).to(PacemateProgramOperation.class);
		bind(ReadFlashOperation.class).to(PacemateReadFlashOperation.class);
		bind(ReadMacAddressOperation.class).to(PacemateReadMacAddressOperation.class);
		bind(ResetOperation.class).to(iSenseResetOperation.class);

		SerialPortConnection connection = new iSenseSerialPortConnection();
		bindInterceptor(
				Matchers.any(),
				Matchers.annotatedWith(ProgrammingMode.class),
				new PacemateProgramInterceptor(connection, getProvider(PacemateHelper.class))
		);

		bind(SerialPortConnection.class).toInstance(connection);
		bind(Connection.class).toInstance(connection);
	}
}
