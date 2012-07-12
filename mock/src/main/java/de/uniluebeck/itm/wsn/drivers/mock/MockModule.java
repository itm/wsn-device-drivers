package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.operation.*;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingModeInterceptor;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Configures Guice dependency injection for a mock device.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public class MockModule extends AbstractModule {

	private final Map<String, String> configuration;

	public MockModule(@Nullable final Map<String, String> configuration) {
		this.configuration = configuration;
	}

	@Override
	protected void configure() {

		MockOperationInterceptor interceptor = new MockOperationInterceptor();
		requestInjection(interceptor);
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(SerialPortProgrammingMode.class), interceptor);

		final TypeLiteral<Map<String, String>> mapLiteral = new TypeLiteral<Map<String, String>>() {
		};

		bind(mapLiteral).annotatedWith(Names.named("configuration"))
				.toInstance(configuration != null ? configuration : Maps.<String, String>newHashMap());

		bind(Device.class).to(MockDevice.class);
		bind(MockConfiguration.class).in(Singleton.class);

		install(new FactoryModuleBuilder()
				.implement(EraseFlashOperation.class, MockEraseFlashOperation.class)
				.implement(GetChipTypeOperation.class, MockGetChipTypeOperation.class)
				.implement(IsNodeAliveOperation.class, DefaultIsNodeAliveOperation.class)
				.implement(ProgramOperation.class, MockProgramOperation.class)
				.implement(ReadFlashOperation.class, MockReadFlashOperation.class)
				.implement(ReadMacAddressOperation.class, MockReadMacAddressOperation.class)
				.implement(ResetOperation.class, MockResetOperation.class)
				.implement(WriteFlashOperation.class, MockWriteFlashOperation.class)
				.implement(WriteMacAddressOperation.class, MockWriteMacAddressOperation.class)
				.build(OperationFactory.class)
		);
	}

}
