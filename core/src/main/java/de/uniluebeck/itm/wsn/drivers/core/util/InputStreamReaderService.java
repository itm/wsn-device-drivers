package de.uniluebeck.itm.wsn.drivers.core.util;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.util.concurrent.AbstractExecutionThreadService;

public class InputStreamReaderService extends AbstractExecutionThreadService {

	private final InputStream inputStream;
	
	private boolean shutdown = false;
	
	public InputStreamReaderService(final InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	@Override
	protected void run() throws Exception {
		try {
			while (!shutdown) {
				readMessage();
				Thread.sleep(50);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void readMessage() throws IOException {
		int available = inputStream.available();
		if (available != 0) {
			while (!shutdown && available != 0) {
				System.out.print((char) inputStream.read());
				available = inputStream.available();
			}
			System.out.println();
		}
	}
	
	@Override
	protected void triggerShutdown() {
		shutdown = true;
	}
}
