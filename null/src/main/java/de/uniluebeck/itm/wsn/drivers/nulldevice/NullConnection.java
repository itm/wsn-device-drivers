package de.uniluebeck.itm.wsn.drivers.nulldevice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionListener;
import de.uniluebeck.itm.wsn.drivers.core.DataAvailableListener;


/**
 * Null implementation for <code>Connection</code>.
 * 
 * @author Malte Legenhausen
 */
public class NullConnection implements Connection {

	@Override
	public InputStream getInputStream() {
		return null;
	}

	@Override
	public OutputStream getOutputStream() {
		return null;
	}

	@Override
	public void connect(final String uri) {
		
	}
	
	@Override
	public void close() throws IOException {
		
	}

	@Override
	public boolean isConnected() {
		return false;
	}

	@Override
	public void addListener(final ConnectionListener listener) {
		
	}

	@Override
	public void removeListener(final ConnectionListener listener) {

	}

	@Override
	public void addListener(final DataAvailableListener listener) {

	}

	@Override
	public void removeListener(final DataAvailableListener listener) {

	}
}
