package de.uniluebeck.itm.wsn.drivers.mock;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wsn.drivers.core.AbstractConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


/**
 * A connection that can be used for testing.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
@Singleton
public class MockConnection extends AbstractConnection {

	private static final Logger log = LoggerFactory.getLogger(MockConnection.class);

	/**
	 * The {@link OutputStream} that is being written to from users of this device.
	 */
	private final PipedOutputStream outputStream = new PipedOutputStream();

	/**
	 * The {@link InputStream} instance from which the mock device can read data that was written to {@link
	 * MockConnection#outputStream}.
	 */
	private final PipedInputStream outputStreamPipedInputStream = new PipedInputStream();

	/**
	 * The {@link InputStream} instance from which is being read from users of this device..
	 */
	private final PipedInputStream inputStream = new PipedInputStream();

	/**
	 * The {@link OutputStream} to which the mock device can write data that will then be piped to {@link
	 * MockConnection#inputStream}.
	 */
	private final PipedOutputStream inputStreamPipedOutputStream = new PipedOutputStream();

	/**
	 * Configuration for this device.
	 */
	private final MockConfiguration configuration;

	@Inject
	public MockConnection(MockConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public void connect(final String uri) throws IOException {

		log.trace("Connecting to MockDevice");

		super.connect(uri);

		synchronized (inputStreamPipedOutputStream) {
			inputStream.connect(inputStreamPipedOutputStream);
		}

		synchronized (outputStreamPipedInputStream) {
			outputStream.connect(outputStreamPipedInputStream);
		}

		setOutputStream(outputStream);
		setInputStream(inputStream);
		setConnected();
	}

	@Override
	public void close() throws IOException {

		log.trace("Closing MockDevice");

		outputStreamPipedInputStream.close();
		inputStreamPipedOutputStream.close();
		inputStream.close();
		outputStream.close();

		super.close();
	}

	@Override
	public int[] getChannels() {
		return configuration.getChannels();
	}

	void writeUpstreamBytes(final byte[] message, final int offset, final int length) {

		if (log.isTraceEnabled()) {
			log.trace("Sending message {}", StringUtils.toHexString(message));
		}

		try {

			synchronized (inputStreamPipedOutputStream) {
				inputStreamPipedOutputStream.write(message, offset, length);
				inputStreamPipedOutputStream.flush();
				signalDataAvailable();
			}

		} catch (IOException e) {
			log.error("IOException while writing to MockConnection.inputStreamPipedOutputStream: {}", e);
			throw new RuntimeException(e);
		}
	}

	void writeUpstreamBytes(final byte[] message) {
		writeUpstreamBytes(message, 0, message.length);
	}
}
