package eu.smartsantander.wsn.drivers.waspmote;

import java.io.IOException;
import java.io.InputStream;

import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.State;
import de.uniluebeck.itm.wsn.drivers.core.event.StateChangedEvent;
import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import de.uniluebeck.itm.wsn.drivers.core.operation.EraseFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.GetChipTypeOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.SendOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteFlashOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.WriteMacAddressOperation;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrame;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.XBeeDigiRequest;
import eu.smartsantander.wsn.drivers.waspmote.multiplexer.WaspmoteConnectionMultiplexer;
import eu.smartsantander.wsn.drivers.waspmote.multiplexer.WaspmoteDataChannel;
import eu.smartsantander.wsn.drivers.waspmote.operation.WaspmoteReadDigiMacAddressOperation;

/**
 * @author TLMAT UC
 */
public class WaspmoteDevice implements Device<WaspmoteVirtualSerialPortConnection> {

	/**
	 * The default timeout that will be waited for available data when in
	 * synchronous mode.
	 */
	private static final int DEFAULT_DATA_AVAILABLE_TIMEOUT = 30;

	/**
	 * The 16 bits node identifier for this device.
	 */
	private int nodeID;

	/**
	 * <code>WaspmoteVirtualSerialPortConnection</code> for this device.
	 */
	private final WaspmoteVirtualSerialPortConnection connection;

	/**
	 * Used to identify all responses relatives to an operation
	 */
	private int deviceOperationID = 0;

	/**
	 * Constructor.
	 *
	 * @param nodeID
	 *            The 16 bits node identifier for this device.
	 * @param connection
	 *            The virtual serial port connection for this device.
	 */
	public WaspmoteDevice(int nodeID, WaspmoteVirtualSerialPortConnection connection) {
		this.connection = connection;
		this.identifyNode(nodeID);
	}

	/**
	 * Constructor.
	 *
	 * @param connection
	 *            The virtual serial port connection for this device.
	 */
	public WaspmoteDevice(WaspmoteVirtualSerialPortConnection connection) {
		this.connection = connection;
	}

	public void identifyNode(int nodeID) {
		this.nodeID = nodeID;
		this.connection.setDeviceNodeID(nodeID);
	}

	/**
	 * Getter of NodeID
	 *
	 * @return The 16 bits node identifier of the device
	 */
	public int getNodeID() {
		return nodeID;
	}

	@Override
	public WaspmoteVirtualSerialPortConnection getConnection() {
		return connection;
	}

	@Override
	public InputStream getInputStream() {
		return connection.getInputStream();
	}

	@Override
	public int[] getChannels() {
		return null;
	}

	@Override
	public GetChipTypeOperation createGetChipTypeOperation() {
		return null;
	}

	@Override
	public ProgramOperation createProgramOperation() {
		return null;
	}

	@Override
	public EraseFlashOperation createEraseFlashOperation() {
		return null;
	}

	@Override
	public WriteFlashOperation createWriteFlashOperation() {
		return null;
	}

	@Override
	public ReadFlashOperation createReadFlashOperation() {
		return null;
	}

	@Override
	public ReadMacAddressOperation createReadMacAddressOperation() {
		int subchannelID = deviceOperationID++;
		final ReadMacAddressOperation operation = new WaspmoteReadDigiMacAddressOperation(this, subchannelID);
		monitor(operation, subchannelID);
		return operation;
	}

	@Override
	public WriteMacAddressOperation createWriteMacAddressOperation() {
		return null;
	}

	@Override
	public ResetOperation createResetOperation() {
		return null;
	}

	@Override
	public SendOperation createSendOperation() {
		return null;
	}


	/**
	 * @param xbeeFrame
	 * @param generateLocalAck
	 * @param operationID
	 */
	public void sendXBeeMessage(XBeeFrame xbeeFrame, boolean generateLocalAck, int operationID) {
		WaspmoteConnectionMultiplexer multiplexer = connection.getSerialPortMultiplexer();
		if (xbeeFrame instanceof XBeeDigiRequest) {
			XBeeDigiRequest req = (XBeeDigiRequest) xbeeFrame;
			try {
				multiplexer.write(req, generateLocalAck, operationID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			throw new IllegalArgumentException("xbeeFrame is not a request.");
		}
	}

	public XBeeFrame receiveXBeeFrame(int operationID) throws TimeoutException, InterruptedException {
		return this.receiveXBeeFrame(operationID, DEFAULT_DATA_AVAILABLE_TIMEOUT);
	}

	public XBeeFrame receiveXBeeFrame(int operationID, int timeout) throws TimeoutException, InterruptedException {
		WaspmoteDataChannel channel = WaspmoteDataChannel.getChannel(this.nodeID);
		XBeeFrame xbeeFrame = channel.getFrame(operationID, timeout);
		if (xbeeFrame == null) {
			throw new TimeoutException("receiveXBeeFrame timed out.");
		}
		return xbeeFrame;
	}

	/**
	 * Register a created operation for monitoring purposes by the device.
	 *
	 * @param <T> Return type of the operation.
	 * @param operation The operation object that has to be monitored.
	 */
	protected <T> Operation<T> monitor(final Operation<T> operation, final int subchannelID) {
		operation.addListener(new OperationListener<T>() {
			@Override
			public void onStateChanged(final StateChangedEvent<T> event) {
				if(event.getNewState() != State.RUNNING) {
					WaspmoteDataChannel.getChannel(nodeID).releaseSubchannel(subchannelID);
				}
			}
		});
		return operation;
	}
}
