package de.uniluebeck.itm.wsn.drivers.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractExecutionThreadService;


/**
 * Service for receiving automatically all available data from an input stream.
 * The received data can then be proceed by a ByteReceiver.
 * 
 * @author Malte Legenhausen
 */
public class InputStreamReaderService extends AbstractExecutionThreadService implements ByteReceiver {

	/**
	 * Sleep between a data receive.
	 */
	private static final int DATA_AVAILABLE_SLEEP = 50;
	
	/**
	 * All <code>ByteReceiver</code> that handle the received data.
	 */
	private final List<ByteReceiver> receivers = new ArrayList<ByteReceiver>();
	
	/**
	 * The <code>InputStream</code> which has to be read from.
	 */
	private InputStream inputStream;
	
	/**
	 * The shutdown flag.
	 */
	private boolean shutdown = false;
	
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	@Override
	protected void run() throws Exception {
		Preconditions.checkState(inputStream != null, "InputStream is not set. Set the InputStream before start.");
		try {
			while (!shutdown) {
				processData();
				Thread.sleep(DATA_AVAILABLE_SLEEP);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void processData() throws IOException {
		int available = inputStream.available();
		if (available != 0) {
			beforeReceive();
			while (!shutdown && available != 0) {
				onReceive((byte) inputStream.read());
				available = inputStream.available();
			}
			afterReceive();
		}
	}
	
	@Override
	protected void triggerShutdown() {
		shutdown = true;
	}

	@Override
	public void beforeReceive() {
		for (final ByteReceiver receiver : receivers.toArray(new ByteReceiver[0])) {
			receiver.beforeReceive();
		}
	}

	@Override
	public void onReceive(byte data) {
		for (final ByteReceiver receiver : receivers.toArray(new ByteReceiver[0])) {
			receiver.onReceive(data);
		}
	}

	@Override
	public void afterReceive() {
		for (final ByteReceiver receiver : receivers.toArray(new ByteReceiver[0])) {
			receiver.afterReceive();
		}
	}
	
	/**
	 * Add a <code>ByteReceiver</code> for data processing.
	 * 
	 * @param receiver The <code>ByteReceiver</code> that has to be added.
	 */
	public void addByteReceiver(final ByteReceiver receiver) {
		receivers.add(receiver);
	}
	
	/**
	 * Remove the given <code>ByteReceiver</code> from the internal list.
	 * 
	 * @param receiver The <code>ByteReceiver</code> that has to be removed.
	 */
	public void removeByteReceiver(final ByteReceiver receiver) {
		receivers.remove(receiver);
	}
}
