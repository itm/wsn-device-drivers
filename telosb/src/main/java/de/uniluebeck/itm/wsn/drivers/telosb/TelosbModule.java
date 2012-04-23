package de.uniluebeck.itm.wsn.drivers.telosb;

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

import javax.annotation.Nullable;
import java.util.Map;

public class TelosbModule extends AbstractModule {

	private final Map<String, String> configuration;

	public TelosbModule() {
		this(null);
	}

	public TelosbModule(@Nullable final Map<String, String> configuration) {
		this.configuration = configuration;
	}

	@Override
	protected void configure() {

		bind(new TypeLiteral<Map<String, String>>() {
		}
		)
				.annotatedWith(Names.named("configuration"))
				.toInstance(configuration != null ? configuration : Maps.<String, String>newHashMap());

		SerialPortConnection connection = new TelosbSerialPortConnection();
		TelosbProgrammingModeInterceptor interceptor = new TelosbProgrammingModeInterceptor();
		requestInjection(interceptor);
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(SerialPortProgrammingMode.class), interceptor);

		bind(Device.class).to(SerialPortDevice.class);
		bind(SerialPortConnection.class).toInstance(connection);
		bind(Connection.class).toInstance(connection);

		install(new FactoryModuleBuilder()
				.implement(EraseFlashOperation.class, TelosbEraseFlashOperation.class)
				.implement(GetChipTypeOperation.class, TelosbGetChipTypeOperation.class)
				.implement(ProgramOperation.class, TelosbProgramOperation.class)
				.implement(ReadFlashOperation.class, TelosbReadFlashOperation.class)
				.implement(ReadMacAddressOperation.class, TelosbReadMacAddressOperation.class)
				.implement(ResetOperation.class, TelosbResetOperation.class)
				.implement(WriteFlashOperation.class, TelosbWriteFlashOperation.class)
				.implement(WriteMacAddressOperation.class, TelosbWriteMacAddressOperation.class)
				.build(OperationFactory.class)
		);
	}

}
