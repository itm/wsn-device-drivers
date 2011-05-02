package de.uniluebeck.itm.wsn.drivers.core.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LockedInputStream extends FilterInputStream implements Lockable {
	
	private boolean locked;
	
	public LockedInputStream(final InputStream in) {
		super(in);
	}
	
	@Override
	public int read() throws IOException {
		if (locked) {
			throw new IOException("Unable to read. InputStream is locked.");
		}
		return in.read();
	}
	
	@Override
	public void setLocked(final boolean locked) {
		this.locked = locked;
	}

}
