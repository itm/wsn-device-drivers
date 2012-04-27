package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.SerialPortDevice;
import de.uniluebeck.itm.wsn.drivers.core.operation.*;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingModeInterceptor;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseResetOperation;

import javax.annotation.Nullable;
import java.util.Map;

public class TriSOSModule extends AbstractModule {

	private final Map<String, String> configuration;

	public TriSOSModule(@Nullable final Map<String, String> configuration) {
		this.configuration = configuration == null ? Maps.<String, String>newHashMap() : configuration;
	}

	@Override
	protected void configure() {

		bind(new TypeLiteral<Map<String, String>>() {
		}
		)
				.annotatedWith(Names.named("configuration"))
				.toInstance(configuration);

		TriSOSSerialPortConnection connection = new TriSOSSerialPortConnection(configuration);

		final SerialPortProgrammingModeInterceptor interceptor = new SerialPortProgrammingModeInterceptor();
		requestInjection(interceptor);
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(SerialPortProgrammingMode.class), interceptor);

		bind(Device.class).to(SerialPortDevice.class);
		bind(SerialPortConnection.class).toInstance(connection);
		bind(Connection.class).toInstance(connection);

		install(new FactoryModuleBuilder()
				.implement(EraseFlashOperation.class, TriSOSEraseFlashOperation.class)
				.implement(GetChipTypeOperation.class, TriSOSGetChipTypeOperation.class)
				.implement(ProgramOperation.class, TriSOSProgramOperation.class)
				.implement(ReadFlashOperation.class, TriSOSReadFlashOperation.class)
				.implement(ReadMacAddressOperation.class, TriSOSReadMacAddressOperation.class)
				.implement(ResetOperation.class, iSenseResetOperation.class)
				.implement(WriteFlashOperation.class, TriSOSWriteFlashOperation.class)
				.implement(WriteMacAddressOperation.class, TriSOSWriteMacAddressOperation.class)
				.build(OperationFactory.class)
		);
	}

}
