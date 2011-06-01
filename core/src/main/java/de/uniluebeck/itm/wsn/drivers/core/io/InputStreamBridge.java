package de.uniluebeck.itm.wsn.drivers.core.io;

import java.io.FilterInputStream;
import java.io.InputStream;

public class InputStreamBridge extends FilterInputStream {
	
	public InputStreamBridge() {
		super(null);
	}

	public InputStream getInputStream() {
		return in;
	}
	
	public void setInputStream(InputStream inputStream) {
		in = inputStream;
	}
}
