package de.uniluebeck.itm.wsn.drivers.core.nulldevice;

import java.io.InputStream;
import java.io.OutputStream;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.ConnectionListener;


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
	public void shutdown(final boolean force) {
		
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

}
