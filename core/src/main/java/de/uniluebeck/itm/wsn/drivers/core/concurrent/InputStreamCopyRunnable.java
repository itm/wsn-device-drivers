package de.uniluebeck.itm.wsn.drivers.core.concurrent;

import com.google.inject.Inject;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionEvent;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Task that copy data from the device connections InputStream to a given OutputStream.
 *
 * @author Malte Legenhausen
 */
public class InputStreamCopyRunnable implements Runnable, ConnectionListener {

	private final static Logger log = LoggerFactory.getLogger(InputStreamCopyRunnable.class);

	private final Connection deviceConnection;

	private final Lock deviceInputStreamLock = new ReentrantLock();

	private final Condition deviceInputStreamDataAvailable = deviceInputStreamLock.newCondition();

	private final OutputStream outputStream;

	@Inject
	public InputStreamCopyRunnable(Connection deviceConnection, @IdleRunnable OutputStream outputStream) {
		this.deviceConnection = deviceConnection;
		this.outputStream = outputStream;
		deviceConnection.addListener(this);
	}

	@Override
	public void run() {
		try {
			while (true) {
				deviceInputStreamLock.lock();
				try {
					deviceInputStreamDataAvailable.await(10, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					return;
				} finally {
					deviceInputStreamLock.unlock();
				}

				InputStream inputStream = deviceConnection.getInputStream();
				if (deviceConnection.isConnected() && inputStream != null) {
					copyAvailableBytes(inputStream);
				}
			}
		} catch (IOException e) {
			log.error("IOException while reading from device InputStream: " + e, e);
		}
	}

	@Override
	public void onDataAvailable(ConnectionEvent event) {
		deviceInputStreamLock.lock();
		try {
			log.trace("onDataAvailable({})", event);
			deviceInputStreamDataAvailable.signal();
		} finally {
			deviceInputStreamLock.unlock();
		}
	}

	private void copyAvailableBytes(final InputStream inputStream) throws IOException {
		int bytesAvailable = inputStream.available();

		if (bytesAvailable > 0) {
			log.trace("Copying {} available bytes", bytesAvailable);
			byte[] buffer = new byte[bytesAvailable];
			final int read = inputStream.read(buffer);
			outputStream.write(buffer, 0, read);
		}
	}

}
