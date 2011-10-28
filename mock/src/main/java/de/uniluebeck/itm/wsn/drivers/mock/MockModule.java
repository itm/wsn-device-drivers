package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.operation.*;

import java.util.Map;

public class MockModule extends AbstractModule {

	private final Map<String, String> configuration;

	public MockModule(final Map<String, String> configuration) {
		this.configuration = configuration;
	}

	@Override
	protected void configure() {

		bind(new TypeLiteral<Map<String, String>>() {})
			.annotatedWith(Names.named("configuration"))
			.toInstance(configuration != null ? configuration : Maps.<String,String>newHashMap());

		bind(EraseFlashOperation.class).to(MockEraseFlashOperation.class);
		bind(GetChipTypeOperation.class).to(MockGetChipTypeOperation.class);
		bind(ProgramOperation.class).to(MockProgramOperation.class);
		bind(ReadFlashOperation.class).to(MockReadFlashOperation.class);
		bind(ReadMacAddressOperation.class).to(MockReadMacAddressOperation.class);
		bind(ResetOperation.class).to(MockResetOperation.class);
		bind(SendOperation.class).to(MockSendOperation.class);
		bind(WriteMacAddressOperation.class).to(MockWriteMacAddressOperation.class);
		bind(WriteFlashOperation.class).to(MockWriteFlashOperation.class);
		bind(Connection.class).to(MockConnection.class);
		bind(MockConfiguration.class).in(Singleton.class);
	}

}
