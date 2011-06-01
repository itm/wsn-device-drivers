package de.uniluebeck.itm.wsn.drivers.core.io;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamBridge extends OutputStream {

	private OutputStream outputStream;
	
	@Override
	public void write(int b) throws IOException {
		if (outputStream == null) {
			throw new IOException("Thie bridged OutputStream is not available");
		}
		outputStream.write(b);
	}
	
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	public OutputStream getOutputStream() {
		return outputStream;
	}
}
