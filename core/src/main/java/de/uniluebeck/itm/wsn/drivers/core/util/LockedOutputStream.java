package de.uniluebeck.itm.wsn.drivers.core.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LockedOutputStream extends FilterOutputStream implements Lockable {

	private boolean locked;
	
	public LockedOutputStream(OutputStream out) {
		super(out);
	}

	@Override
	public void write(int b) throws IOException {
		if (locked) {
			throw new IOException("Unable to write. This OutputStream is locked.");
		}
		super.write(b);
	}
	
	@Override
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}
