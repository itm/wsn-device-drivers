package de.uniluebeck.itm.wsn.drivers.core.mockdevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.wsn.drivers.core.ConnectionEvent;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionListener;
import de.uniluebeck.itm.wsn.drivers.core.MessagePacket;
import de.uniluebeck.itm.wsn.drivers.core.ObserverableDevice;
import de.uniluebeck.itm.wsn.drivers.core.event.MessageEvent;
import de.uniluebeck.itm.wsn.drivers.core.mockdevice.MockConnection.MockListener;
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
public class MockDevice extends ObserverableDevice<MockConnection> implements MockListener {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MockDevice.class);
	
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
		
		connection.addListener(new ConnectionListener() {
			@Override
			public void onConnectionChange(final ConnectionEvent event) {
				MockDevice.this.onConnectionChanged(event.isConnected());
			}
		});
		onConnectionChanged(connection.isConnected());
	}
	
	/**
	 * Register or deregister the mock listener on connection change.
	 * 
	 * @param connected The connection state.
	 */
	private void onConnectionChanged(final boolean connected) {
		if (connected) {
			connection.addMockListener(this);
		} else {
			connection.removeMockListener(this);
		}
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
	public void onData(final byte[] bytes) {
		final MessagePacket messagePacket = MessagePacket.parse(bytes, 0, bytes.length);
		LOG.debug("Emitting message packet: {}", messagePacket);
		fireMessagePacketEvent(new MessageEvent<MessagePacket>(this, messagePacket));
	}
}
