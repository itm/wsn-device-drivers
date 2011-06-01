package es.unican.tlmat.wsn.drivers.waspmote;

import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import de.uniluebeck.itm.wsn.drivers.core.operation.*;
import es.unican.tlmat.wsn.drivers.waspmote.frame.XBeeFrame;
import es.unican.tlmat.wsn.drivers.waspmote.frame.xbeeDigi.XBeeDigiRequest;
import es.unican.tlmat.wsn.drivers.waspmote.multiplexer.WaspmoteConnectionMultiplexer;
import es.unican.tlmat.wsn.drivers.waspmote.multiplexer.WaspmoteDataChannel;
import es.unican.tlmat.wsn.drivers.waspmote.operation.WaspmoteReadDigiMacAddressOperation;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author TLMAT UC
 */
public class WaspmoteDevice implements Device<WaspmoteMultiplexedSerialPortConnection> {

	private static final int DEFAULT_DATA_AVAILABLE_TIMEOUT = 30;

	private final int nodeID;
	private final WaspmoteMultiplexedSerialPortConnection multiplexedConnection;

	public WaspmoteDevice(int nodeID, WaspmoteMultiplexedSerialPortConnection multiplexedConnection) {
		this.nodeID = nodeID;
		this.multiplexedConnection = multiplexedConnection;
	}

	public int getNodeID() {
		return nodeID;
	}

	@Override
	public WaspmoteMultiplexedSerialPortConnection getConnection() {
		return multiplexedConnection;
	}

	protected <T> void monitorState(final Operation<T> operation) {
	}

	@Override
	public InputStream getInputStream() {
		return multiplexedConnection.getInputStream();
	}

	@Override
	public int[] getChannels() {
		return null;
	}

	// It should be present in the Device Interface
	public void shutdown() {
		WaspmoteDataChannel.getChannel(nodeID).shutdownChannel();
		multiplexedConnection.shutdown(false);
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
		final ReadMacAddressOperation operation = new WaspmoteReadDigiMacAddressOperation(this);
		monitorState(operation);
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

	public void sendXBeeMessage(XBeeFrame xbeeFrame, boolean generateLocalAck) {
		WaspmoteConnectionMultiplexer multiplexer = multiplexedConnection.getSerialPortMultiplexer();
		if (xbeeFrame instanceof XBeeDigiRequest) {
			XBeeDigiRequest req = (XBeeDigiRequest) xbeeFrame;
			try {
				multiplexer.write(req, generateLocalAck);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			throw new IllegalArgumentException("xbeeFrame is not a request.");
		}
	}

	public XBeeFrame receiveXBeeFrame() throws TimeoutException, InterruptedException {
		return this.receiveXBeeFrame(DEFAULT_DATA_AVAILABLE_TIMEOUT);
	}

	public XBeeFrame receiveXBeeFrame(int timeout) throws TimeoutException, InterruptedException {
		WaspmoteDataChannel channel = WaspmoteDataChannel.getChannel(this.nodeID);
		XBeeFrame xbeeFrame =  channel.getFrame(timeout);
		if(xbeeFrame==null) {
			throw new TimeoutException("receiveXBeeFrame timed out.");
		}
		return xbeeFrame;
	}

}
