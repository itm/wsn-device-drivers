package de.uniluebeck.itm.wsn.drivers.core.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * This class allows dynamically bind an InputStream.
 * Usable for null object pattern.
 * 
 * @author Malte Legenhausen
 */
public class InputStreamBridge extends FilterInputStream {

	public InputStreamBridge() {
		super(null);
	}

	private void checkInputStream() throws IOException {
		if (in == null) {
			throw new IOException("This bridged InputStream is not available");
		}
	}

	@Override
	public int read() throws IOException {
		checkInputStream();
		return super.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		checkInputStream();
		return super.read(b, off, len);
	}

	@Override
	public int available() throws IOException {
		checkInputStream();
		return super.available();
	}

	@Override
	public void close() throws IOException {
		checkInputStream();
		super.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		if (in != null) {
			super.mark(readlimit);
		}
	}

	@Override
	public boolean markSupported() {
		boolean result = false;
		if (in != null) {
			result = super.markSupported();
		}
		return result;
	}

	@Override
	public synchronized void reset() throws IOException {
		checkInputStream();
		super.reset();
	}

	public InputStream getInputStream() {
		return in;
	}

	public void setInputStream(InputStream inputStream) {
		in = inputStream;
	}
}
