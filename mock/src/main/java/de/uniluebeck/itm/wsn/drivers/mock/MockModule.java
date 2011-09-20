package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.SendOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteMacAddressOperation;

public class MockModule extends AbstractModule {

	@Override
	protected void configure() {
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
