package de.uniluebeck.itm.wsn.drivers.mock;

import java.io.IOException;
import java.io.InputStream;
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
	
	@Override
	public InputStream getSaveInputStream() {
		// TODO Auto-generated method stub
		return inputStream;
	}
	
	@Override
	public void setOperationRunning(boolean running) {
		// TODO Auto-generated method stub
		
	}
}
