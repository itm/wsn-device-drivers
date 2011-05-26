package es.unican.tlmat.wsn.drivers.waspmote.multiplexer;

import es.unican.tlmat.util.DoubleByte;
import es.unican.tlmat.wsn.drivers.waspmote.frame.XBeeFrame;
import es.unican.tlmat.wsn.drivers.waspmote.frame.xbeeDigi.XBeeDigiRequest;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TLMAT UC
 */
public class WaspmoteConnectionMultiplexer implements SerialPortEventListener {

	private static final Logger LOG = LoggerFactory.getLogger(WaspmoteConnectionMultiplexer.class);

	private final HashMap<Byte, OperationAddressInfo> localAckLookupTable = new HashMap<Byte, OperationAddressInfo>();;
	private final HashMap<Integer, Thread> serviceFrameLookupTable = new HashMap<Integer, Thread>();

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

	public synchronized void write(XBeeDigiRequest xbeeRequest, boolean generateLocalAck) throws IOException {
		byte[] xbeeBinaryFrame = XBeeBinaryFrameHelper.createBinaryFrame(xbeeRequest, generateLocalAck);
		Thread currentThreadID = Thread.currentThread();
		if (generateLocalAck) {
			OperationAddressInfo oai = new OperationAddressInfo(xbeeRequest.getNodeID16BitValue(), currentThreadID);
			localAckLookupTable.put(xbeeBinaryFrame[4], oai);
		}
		// DoubleByte cmdTypeAndReqID = new
		// DoubleByte(xbeeRequest.getPayload()[0], xbeeRequest.getPayload()[1]);
		// serviceFrameLookupTable.put(cmdTypeAndReqID.get16BitValue(),
		// currentThreadID);
		serviceFrameLookupTable.put(xbeeRequest.getNodeID16BitValue(), currentThreadID);

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
			XBeeFrame xbeeFrame = null;
			Thread consumerTID = null;
			;
			int frameType = (int) (response[3] & 0x00ff);
			switch (frameType) {
			case XBeeFrame.TRANSMIT_STATUS:
				OperationAddressInfo oai = localAckLookupTable.remove(response[4]);
				if (oai != null) {
					xbeeFrame = XBeeBinaryFrameHelper.createXBeeDigiStatusResponse(oai.getNodeID(), response);
					consumerTID = oai.getThreadID();
				}
				break;
			case XBeeFrame.RECEIVE_PACKET:
				xbeeFrame = XBeeBinaryFrameHelper.createXBeeDigiResponse(response);
				// Cambiar por cmdTypeAndReqID
				consumerTID = serviceFrameLookupTable.get(xbeeFrame.getNodeID16BitValue());
				break;
			default:
				break;
			}
			if (xbeeFrame != null) {
				WaspmoteDataChannel channel = WaspmoteDataChannel.getChannel(xbeeFrame.getNodeID16BitValue());
				try {
					channel.putFrame(consumerTID, xbeeFrame);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
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
