package de.uniluebeck.itm.wsn.drivers.telosb;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;

import com.google.inject.name.Names;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.serialport.ProgrammingMode;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;

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

		bind(new TypeLiteral<Map<String, String>>() {})
			.annotatedWith(Names.named("configuration"))
			.toInstance(configuration != null ? configuration : Maps.<String, String>newHashMap());

		bind(EraseFlashOperation.class).to(TelosbEraseFlashOperation.class);
		bind(GetChipTypeOperation.class).to(TelosbGetChipTypeOperation.class);
		bind(ProgramOperation.class).to(TelosbProgramOperation.class);
		bind(ResetOperation.class).to(TelosbResetOperation.class);
		bind(WriteFlashOperation.class).to(TelosbWriteFlashOperation.class);
		
		SerialPortConnection connection = new TelosbSerialPortConnection();
		bindInterceptor(
				Matchers.any(),
				Matchers.annotatedWith(ProgrammingMode.class),
				new TelosbProgramInterceptor(connection, getProvider(BSLTelosb.class))
		);

		bind(SerialPortConnection.class).toInstance(connection);
		bind(Connection.class).toInstance(connection);
	}

}
