package de.uniluebeck.itm.wsn.drivers.pacemate;

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
import de.uniluebeck.itm.wsn.drivers.isense.iSenseResetOperation;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseSerialPortConnection;

import javax.annotation.Nullable;
import java.util.Map;

public class PacemateModule extends AbstractModule {

	private final Map<String, String> configuration;

	public PacemateModule() {
		this(null);
	}

	public PacemateModule(@Nullable final Map<String, String> configuration) {
		this.configuration = configuration;
	}

	@Override
	protected void configure() {

		bind(new TypeLiteral<Map<String, String>>() {
		}
		)
				.annotatedWith(Names.named("configuration"))
				.toInstance(configuration != null ? configuration : Maps.<String, String>newHashMap());

		SerialPortConnection connection = new iSenseSerialPortConnection();
		PacemateProgrammingModeInterceptor interceptor = new PacemateProgrammingModeInterceptor();
		requestInjection(interceptor);
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(SerialPortProgrammingMode.class), interceptor);

		bind(Device.class).to(SerialPortDevice.class);
		bind(SerialPortConnection.class).toInstance(connection);
		bind(Connection.class).toInstance(connection);

		install(new FactoryModuleBuilder()
				.implement(EraseFlashOperation.class, PacemateEraseFlashOperation.class)
				.implement(GetChipTypeOperation.class, PacemateGetChipTypeOperation.class)
				.implement(ProgramOperation.class, PacemateProgramOperation.class)
				.implement(ReadFlashOperation.class, PacemateReadFlashOperation.class)
				.implement(ReadMacAddressOperation.class, PacemateReadMacAddressOperation.class)
				.implement(ResetOperation.class, iSenseResetOperation.class)
				.implement(WriteFlashOperation.class, PacemateWriteFlashOperation.class)
				.implement(WriteMacAddressOperation.class, PacemateWriteMacAddressOperation.class)
				.build(OperationFactory.class)
		);
	}
}
