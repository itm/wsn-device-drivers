package de.uniluebeck.itm.wsn.drivers.core.mockdevice;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import de.uniluebeck.itm.wsn.drivers.core.AbstractConnection;


/**
 * A connection that can be used for testing.
 * 
 * @author Malte Legenhausen
 */
public class MockConnection extends AbstractConnection {
	
	/**
	 * The <code>PipedOutputStream</code>.
	 */
	private PipedOutputStream outputStream;
	
	/**
	 * The <code>PipedInputStream</code>.
	 */
	private PipedInputStream inputStream;
	
	@Override
	public void connect(final String uri) {
		outputStream = new PipedOutputStream();
		try {
			inputStream = new PipedInputStream(outputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		setOutputStream(outputStream);
		setInputStream(inputStream);
		setConnected(true);
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		setConnected(false);
	}
}
