package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.operation.*;
import de.uniluebeck.itm.wsn.drivers.core.serialport.ProgrammingMode;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgramInterceptor;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseSerialPortConnection;

import javax.annotation.Nullable;
import java.util.Map;

public class TriSOSModule extends AbstractModule {

	private final Map<String, String> configuration;

	public TriSOSModule() {
		this(null);
	}

	public TriSOSModule(@Nullable final Map<String, String> configuration) {
		this.configuration = configuration;
	}

	@Override
	protected void configure() {

		bind(new TypeLiteral<Map<String, String>>() {})
			.annotatedWith(Names.named("configuration"))
			.toInstance(configuration != null ? configuration : Maps.<String,String>newHashMap());

		bind(ProgramOperation.class).to(TriSOSProgramOperation.class); // Programming
		bind(ResetOperation.class).to(TriSOSResetOperation.class);     // Resetting
		bind(TriSOSConfiguration.class).in(Singleton.class);           // Configuration

                bind(ReadMacAddressOperation.class).to(TriSOSReadMacAddressOperation.class);

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
