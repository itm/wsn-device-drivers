package de.uniluebeck.itm.devicedriver.serialport;

import java.nio.ByteBuffer;
import java.util.Arrays;

import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.MessagePlainText;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;

public class MessagePlainTextReceiver implements ByteReceiver {
	/**
	 * <code>ByteBuffer</code> for the input of <code>MessagePlainText</code> objects.
	 */
	private final ByteBuffer buffer = ByteBuffer.allocate(2048);
	
	private AbstractSerialPortDevice device;
	
	@Override
	public void setDevice(Device device) {
		this.device = (AbstractSerialPortDevice) device;
	}

	@Override
	public void beforeReceive() {
		buffer.clear();
	}

	@Override
	public void onReceive(byte data) {
		if (buffer.hasRemaining()) {
			buffer.put(data);
		} else {
			sendMessagePlainText();
		}
	}

	@Override
	public void afterReceive() {
		sendMessagePlainText();
	}
	
	private void sendMessagePlainText() {
		// Notify listeners
		final byte[] text = Arrays.copyOf(buffer.array(), buffer.position() + 1);		
		final MessagePlainText message = new MessagePlainText(text);
		device.fireMessagePlainTextEvent(new MessageEvent<MessagePlainText>(this, message));
		buffer.clear();
	}
}
