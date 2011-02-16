package de.uniluebeck.itm.devicedriver.nulldevice;

import java.io.InputStream;
import java.io.OutputStream;

import de.uniluebeck.itm.devicedriver.Connection;
import de.uniluebeck.itm.devicedriver.ConnectionListener;

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
	public void connect(String uri) {
		
	}

	@Override
	public void shutdown(boolean force) {
		
	}

	@Override
	public boolean isConnected() {
		return false;
	}

	@Override
	public void addListener(ConnectionListener listener) {
		
	}

	@Override
	public void removeListener(ConnectionListener listener) {

	}

}
