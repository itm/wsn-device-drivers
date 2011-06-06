package eu.smartsantander.wsn.drivers.waspmote.multiplexer;

import de.uniluebeck.itm.wsn.drivers.core.util.DoubleByte;
import eu.smartsantander.wsn.drivers.waspmote.frame.smartSantander.SmartSantanderFrame;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.XBeeFrame;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.XBeeDigiRequest;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.XBeeDigiResponse;
import eu.smartsantander.wsn.drivers.waspmote.frame.xbee.xbeeDigi.XBeeDigiStatusResponse;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TLMAT UC
 */
public class WaspmoteConnectionMultiplexer implements SerialPortEventListener {

	private static final Logger LOG = LoggerFactory.getLogger(WaspmoteConnectionMultiplexer.class);

	private final HashMap<Byte, OperationAddressInfo> localAckLookupTable = new HashMap<Byte, OperationAddressInfo>();;
	private final HashMap<Integer, Integer> driverLayerFramesLookupTable = new HashMap<Integer, Integer>();
	private final HashMap<Integer, OutputStream> upperLayerFramesLookupTable = new HashMap<Integer, OutputStream>();

	private final InputStream inputStream;
	private final OutputStream outputStream;

	/**
	 * Buffer to hold the whole reading
	 */
	private static int READ_BUFFER_LENGTH = 256;
	private byte[] readBuffer = new byte[READ_BUFFER_LENGTH];

	/**
	 * @param inputStream
	 * @param outputStream
	 */
	public WaspmoteConnectionMultiplexer(InputStream inputStream, OutputStream outputStream) {
		super();
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	public synchronized InputStream registerNode(int nodeID) {
		try {
			PipedOutputStream multiplexedChannelWriteStream = new PipedOutputStream();
			PipedInputStream multiplexedChannelReadStream = new PipedInputStream(multiplexedChannelWriteStream);
			upperLayerFramesLookupTable.put(nodeID, multiplexedChannelWriteStream);
			return multiplexedChannelReadStream;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public synchronized void deregisterNode(int nodeID) {
		upperLayerFramesLookupTable.remove(nodeID);
		WaspmoteDataChannel.getChannel(nodeID).shutdownChannel();
	}

	public synchronized boolean isAnyNodeRegistered() {
		return upperLayerFramesLookupTable.size() > 0;
	}

	public synchronized void write(XBeeDigiRequest xbeeRequest, boolean generateLocalAck, int consumerSubchannelID)
			throws IOException {
		byte[] xbeeBinaryFrame = XBeeBinaryFrameHelper.createBinaryFrame(xbeeRequest, generateLocalAck);
		if (generateLocalAck) {
			OperationAddressInfo oai = new OperationAddressInfo(xbeeRequest.getNodeID16BitValue(), consumerSubchannelID);
			localAckLookupTable.put(xbeeBinaryFrame[4], oai);
		}
		//@formatter:off
		// DoubleByte cmdTypeAndReqID = new DoubleByte(xbeeRequest.getPayload()[0], xbeeRequest.getPayload()[1]);
		// syncFramesLookupTable.put(cmdTypeAndReqID.get16BitValue(), subchannelID);
		driverLayerFramesLookupTable.put(xbeeRequest.getNodeID16BitValue(), consumerSubchannelID);
		// With the above, only one concurrent sub-channel can coexist for each synchronous operation.
		//Use cmdTypeAndReqID instead of nodeID for adding concurrent operations support.
		//@formatter:on

		outputStream.write(xbeeBinaryFrame);
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:
			processIncomingBinaryFrame();
			break;
		default:
			LOG.debug("Serial event (other than data available): " + event);
			break;
		}
	}

	private void processIncomingBinaryFrame() {
		try {
			byte[] response = readXbeeFrameFromSerialPort();
			int frameType = (int) (response[3] & 0x00ff);
			switch (frameType) {
			case XBeeFrame.TRANSMIT_STATUS_DIGI:
				OperationAddressInfo oai = localAckLookupTable.remove(response[4]);
				if (oai != null) {
					XBeeDigiStatusResponse xbeeStatusResponse = XBeeBinaryFrameHelper.createXBeeDigiStatusResponse(oai.getNodeID(), response);
					WaspmoteDataChannel channel = WaspmoteDataChannel.getChannel(xbeeStatusResponse.getNodeID16BitValue());
					channel.putFrame(oai.getSubchannelID(), xbeeStatusResponse);
				}
				break;
			case XBeeFrame.RECEIVE_PACKET_DIGI:
				XBeeDigiResponse xbeeDigiResponse = XBeeBinaryFrameHelper.createXBeeDigiResponse(response);
				DoubleByte cmdTypeAndReqID = new DoubleByte(xbeeDigiResponse.getPayload()[0], xbeeDigiResponse.getPayload()[1]);
				if (cmdTypeAndReqID.equals(new DoubleByte(SmartSantanderFrame.SERVICE_FRAME_HEADER))) {
					OutputStream multiplexedChannelWriteStream = upperLayerFramesLookupTable.get(xbeeDigiResponse.getNodeID16BitValue());
					if(multiplexedChannelWriteStream != null) {
						multiplexedChannelWriteStream.write(xbeeDigiResponse.getPayload().length);
						multiplexedChannelWriteStream.write(xbeeDigiResponse.getPayload());
					}
				} else {
					WaspmoteDataChannel channel = WaspmoteDataChannel.getChannel(xbeeDigiResponse.getNodeID16BitValue());
					channel.putFrame(driverLayerFramesLookupTable.get(xbeeDigiResponse.getNodeID16BitValue()), xbeeDigiResponse);
					// Substitute nodeID with cmdTypeAndReqID if needed (see write function above)
				}
				break;
			case XBeeFrame.TRANSMIT_STATUS_802_15_4:
			case XBeeFrame.RECEIVE_PACKET_802_15_4:
			default:
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private byte[] readXbeeFrameFromSerialPort() throws IOException {
		int nbytes = 0;
		do {
			nbytes = inputStream.read(readBuffer, 0, 1);
		} while (readBuffer[0] != 0x7E);
		nbytes += inputStream.read(readBuffer, 1, 2);
		DoubleByte xbeePayloadSize = new DoubleByte(readBuffer[1], readBuffer[2]);
		nbytes += inputStream.read(readBuffer, 3, xbeePayloadSize.get16BitValue() + 1);
		byte[] xbeeFrame = new byte[nbytes];
		System.arraycopy(readBuffer, 0, xbeeFrame, 0, xbeeFrame.length);
		return xbeeFrame;
	}

}
