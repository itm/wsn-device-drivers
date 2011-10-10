package de.uniluebeck.itm.wsn.drivers.test;

import java.nio.ByteBuffer;
import java.util.Arrays;



/**
 * A byte receiver that stores the data in an internal buffer with a capacity of 2048 bytes.
 * 
 * @author Malte Legenhausen
 */
public class BufferedByteReceiver implements ByteReceiver {

	/**
	 * The internal buffer size.
	 */
	private static final int BUFFER_SIZE = 2048;
	
	/**
	 * The internal buffer.
	 */
	private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
	
	@Override
	public void beforeReceive() {
		buffer.clear();
	}

	@Override
	public void onReceive(byte data) {
		if (buffer.hasRemaining()) {
			buffer.put(data);
		} else {
			afterReceive();
		}
	}

	@Override
	public void afterReceive() {
		onBytesReceived(buffer.array(), buffer.arrayOffset(), buffer.position() - buffer.arrayOffset());
	}
	
	/**
	 * Override this method to proceed received data.
	 * Default implementation print the received data on the console.
	 * 
	 * @param bytes The received data.
	 * @param offset The offset of the received byte array.
	 * @param length The length of the used data range.
	 */
	public void onBytesReceived(byte[] bytes, int offset, int length) {
		final byte[] text = Arrays.copyOf(bytes, length);
		System.out.println(new String(text));
		buffer.clear();
	}
}
