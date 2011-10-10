package de.uniluebeck.itm.wsn.drivers.test;

import java.util.Arrays;


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
