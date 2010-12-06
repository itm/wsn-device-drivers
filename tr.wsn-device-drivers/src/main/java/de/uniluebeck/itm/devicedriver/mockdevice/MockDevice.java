package de.uniluebeck.itm.devicedriver.mockdevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.ConnectionEvent;
import de.uniluebeck.itm.devicedriver.ConnectionListener;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.ObserverableDevice;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;
import de.uniluebeck.itm.devicedriver.mockdevice.MockConnection.MockListener;
import de.uniluebeck.itm.devicedriver.operation.EraseFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.GetChipTypeOperation;
import de.uniluebeck.itm.devicedriver.operation.ProgramOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.devicedriver.operation.ResetOperation;
import de.uniluebeck.itm.devicedriver.operation.SendOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteFlashOperation;
import de.uniluebeck.itm.devicedriver.operation.WriteMacAddressOperation;


/**
 * Mock device that can be used for testing.
 * 
 * @author Malte Legenhausen
 */
public class MockDevice extends ObserverableDevice implements MockListener {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(MockDevice.class);
	
	private final MockConfiguration configuration;
	
	private final MockConnection connection;
	
	public MockDevice(MockConnection connection) {
		this(new MockConfiguration(), connection);
	}
	
	public MockDevice(MockConfiguration configuration, MockConnection connection) {
		this.configuration = configuration;
		this.connection = connection;
		
		connection.addListener(new ConnectionListener() {
			@Override
			public void onConnectionChange(ConnectionEvent event) {
				MockDevice.this.onConnectionChange(event);
			}
		});
	}
	
	private void onConnectionChange(ConnectionEvent event) {
		if (event.isConnected()) {
			connection.addMockListener(this);
		} else {
			connection.removeMockListener(this);
		}
	}
	
	@Override
	public Connection getConnection() {
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
	public void onData(byte[] bytes) {
		MessagePacket messagePacket = MessagePacket.parse(bytes, 0, bytes.length);
		logger.debug("Emitting message packet: {}", messagePacket);
		fireMessagePacketEvent(new MessageEvent<MessagePacket>(this, messagePacket));
	}
}
