package de.uniluebeck.itm.wsn.drivers.jennic;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
<<<<<<< HEAD
import de.uniluebeck.itm.wsn.drivers.core.Connection;
=======
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.SerialPortDevice;
>>>>>>> develop
import de.uniluebeck.itm.wsn.drivers.core.operation.*;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingMode;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortProgrammingModeInterceptor;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseResetOperation;
import de.uniluebeck.itm.wsn.drivers.isense.iSenseSerialPortConnection;

import javax.annotation.Nullable;
import java.util.Map;

public class JennicModule extends AbstractModule {

<<<<<<< HEAD
    @Override
=======
	private final Map<String, String> configuration;

	public JennicModule() {
		this(null);
	}

	public JennicModule(@Nullable final Map<String, String> configuration) {
		this.configuration = configuration;
	}

	@Override
>>>>>>> develop
	protected void configure() {

		bind(new TypeLiteral<Map<String, String>>() {
		}
		)
				.annotatedWith(Names.named("configuration"))
				.toInstance(configuration != null ? configuration : Maps.<String, String>newHashMap());

		SerialPortConnection connection = new iSenseSerialPortConnection();
		SerialPortProgrammingModeInterceptor programmingModeInterceptor = new SerialPortProgrammingModeInterceptor();
		requestInjection(programmingModeInterceptor);
		bindInterceptor(
				Matchers.any(),
				Matchers.annotatedWith(SerialPortProgrammingMode.class),
				programmingModeInterceptor
		);

		bind(Device.class).to(SerialPortDevice.class);
		bind(Connection.class).toInstance(connection);
		bind(SerialPortConnection.class).toInstance(connection);

		install(new FactoryModuleBuilder()
				.implement(EraseFlashOperation.class, JennicEraseFlashOperation.class)
				.implement(GetChipTypeOperation.class, JennicGetChipTypeOperation.class)
				.implement(ProgramOperation.class, JennicProgramOperation.class)
				.implement(ReadFlashOperation.class, JennicReadFlashOperation.class)
				.implement(ReadMacAddressOperation.class, JennicReadMacAddressOperation.class)
				.implement(ResetOperation.class, iSenseResetOperation.class)
				.implement(WriteFlashOperation.class, JennicWriteFlashOperation.class)
				.implement(WriteMacAddressOperation.class, JennicWriteMacAddressOperation.class)
				.build(OperationFactory.class)
		);
	}
}
