package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.SerialPortDevice;
import de.uniluebeck.itm.wsn.drivers.core.operation.*;

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

	public MockModule() {
		this(null);
	}

	public MockModule(@Nullable final Map<String, String> configuration) {
		this.configuration = configuration;
	}

	@Override
	protected void configure() {

		bind(new TypeLiteral<Map<String, String>>() {
		}
		)
				.annotatedWith(Names.named("configuration"))
				.toInstance(configuration != null ? configuration : Maps.<String, String>newHashMap());

		bind(Device.class).to(SerialPortDevice.class);
		bind(Connection.class).to(MockConnection.class);
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
