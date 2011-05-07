package de.uniluebeck.itm.wsn.drivers.core.mockdevice;

import java.io.InputStream;

import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.SendOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteMacAddressOperation;


/**
 * Mock device that can be used for testing.
 * 
 * @author Malte Legenhausen
 */
public class MockDevice implements Device<MockConnection> {
	
	/**
	 * The configuration of this mock device.
	 */
	private final MockConfiguration configuration;
	
	/**
	 * The connection for this device.
	 */
	private final MockConnection connection;
	
	/**
	 * Constructor.
	 * 
	 * @param connection The connection for this device.
	 */
	public MockDevice(final MockConnection connection) {
		this(new MockConfiguration(), connection);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param configuration The connection for this device.
	 * @param connection An alternative configuration for the mock device.
	 */
	public MockDevice(final MockConfiguration configuration, final MockConnection connection) {
		this.configuration = configuration;
		this.connection = connection;
	}
	
	@Override
	public MockConnection getConnection() {
		return connection;
	}

	@Override
	public int[] getChannels() {
		return configuration.getChannels();
	}

	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		return new MockGetChipTypeOperation(configuration);
	}

	@Override
	public ProgramOperation createProgramOperation() {
		return new MockProgramOperation(configuration);
	}

	@Override
	public EraseFlashOperation createEraseFlashOperation() {
		return new MockEraseFlashOperation(configuration);
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		return new MockWriteFlashOperation(configuration);
	}

	@Override
	public ReadFlashOperation createReadFlashOperation() {
		return new MockReadFlashOperation(configuration);
	}

	@Override
	public ReadMacAddressOperation createReadMacAddressOperation() {
		return new MockReadMacAddress(configuration);
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		return new MockWriteMacAddressOperation(configuration);
	}

	@Override
	public ResetOperation createResetOperation() {
		return new MockResetOperation(connection);
	}

	@Override
	public SendOperation createSendOperation() {
		return new MockSendOperation(connection);
	}
	
	@Override
	public InputStream getInputStream() {
		return getConnection().getInputStream();
	}
}
