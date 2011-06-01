package de.uniluebeck.itm.wsn.drivers.core.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamBridge extends InputStream {

	private InputStream inputStream;
	
	@Override
	public int read() throws IOException {
		if (inputStream == null) {
			throw new IOException("The bridged InputStream is not available");
		}
		return inputStream.read();
	}

	public InputStream getInputStream() {
		return inputStream;
	}
	
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
}
