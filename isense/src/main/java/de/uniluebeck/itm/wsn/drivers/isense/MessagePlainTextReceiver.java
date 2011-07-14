package de.uniluebeck.itm.wsn.drivers.isense;

import java.util.Arrays;

import de.uniluebeck.itm.wsn.drivers.core.util.BufferedByteReceiver;

public class MessagePlainTextReceiver extends BufferedByteReceiver {

	@Override
	public void onBytesReceived(byte[] bytes, int offset, int length) {
		final byte[] text = Arrays.copyOf(bytes, length);		
		final MessagePlainText message = new MessagePlainText(text);
		onMessagePlainTextReceived(message);
	}
	
	public void onMessagePlainTextReceived(MessagePlainText message) {
		System.out.println(new String(message.getContent()).substring(1));
	}
}
