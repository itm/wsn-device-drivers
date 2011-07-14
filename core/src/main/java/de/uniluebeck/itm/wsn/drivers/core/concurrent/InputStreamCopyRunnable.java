package de.uniluebeck.itm.wsn.drivers.core.concurrent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionEvent;
import de.uniluebeck.itm.wsn.drivers.core.DataAvailableListener;


/**
 * Task that copy data from the connection InputStream to a given OutputStream.
 * 
 * @author Malte Legenhausen
 */
public class InputStreamCopyRunnable implements Runnable, DataAvailableListener {
	
	private static final Logger LOG = Logger.getLogger(InputStreamCopyRunnable.class);

	private static final int DATA_AVAILABLE_TIMEOUT = 50;
	
	private final Connection connection;
	
	private final Lock deviceInputStreamLock = new ReentrantLock();

	private final Condition deviceInputStreamDataAvailable = deviceInputStreamLock.newCondition();
	
	private final OutputStream outputStream;
	
	@Inject
	public InputStreamCopyRunnable(Connection connection, @Idle OutputStream outputStream) {
		this.connection = connection;
		this.outputStream = outputStream;
		connection.addListener(this);
	}
	
	@Override
	public void run() {
		try {
			InputStream inputStream = connection.getInputStream();
			while (true) {
				deviceInputStreamLock.lock();
				try {
					deviceInputStreamDataAvailable.await(DATA_AVAILABLE_TIMEOUT, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					LOG.trace("Interrupted", e);
					return;
				} finally {
					deviceInputStreamLock.unlock();
				}

				if (connection.isConnected()) {
					copyAvailableBytes(inputStream);
				}
			}
		} catch (IOException e) {
			LOG.error("IOException while reading from device InputStream: " + e, e);
		}
	}
	
	@Override
	public void dataAvailable(ConnectionEvent event) {
		deviceInputStreamLock.lock();
		try {
			deviceInputStreamDataAvailable.signal();
		} finally {
			deviceInputStreamLock.unlock();
		}
	}
	
	private void copyAvailableBytes(final InputStream inputStream) throws IOException {
		int bytesAvailable = inputStream.available();
		
		if (bytesAvailable > 0) {
			byte[] buffer = new byte[bytesAvailable];
			final int read = inputStream.read(buffer);
			outputStream.write(buffer, 0, read);
		}
	}

}
