package de.uniluebeck.itm.wsn.drivers.core.io;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamBridge extends OutputStream {

	private OutputStream outputStream;
	
	private void checkOutputStream() throws IOException {
		if (outputStream == null) {
			throw new IOException("Thie bridged OutputStream is not available");
		}
	}
	
	@Override
	public void write(int b) throws IOException {
		checkOutputStream();
		outputStream.write(b);
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		checkOutputStream();
		outputStream.write(b);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		checkOutputStream();
		outputStream.write(b, off, len);
	}
	
	@Override
	public void flush() throws IOException {
		checkOutputStream();
		outputStream.flush();
	}
	
	@Override
	public void close() throws IOException {
		checkOutputStream();
		outputStream.close();
	}
	
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	public OutputStream getOutputStream() {
		return outputStream;
	}
}
