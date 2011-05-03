package de.uniluebeck.itm.wsn.drivers.core.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * <code>FilterOutputStream</code> implementation that can be locked.
 * When locked you are not able to write on the stream.
 * For locking use the <code>setLocked</code> method.
 * 
 * @author Malte Legenhausen
 */
public class LockedOutputStream extends FilterOutputStream implements Lockable {

	/**
	 * The lock flag.
	 */
	private boolean locked;
	
	/**
	 * Constructor.
	 * 
	 * @param out The wrapped <code>OutputStream</code>.
	 */
	public LockedOutputStream(final OutputStream out) {
		super(out);
	}

	@Override
	public void write(final int b) throws IOException {
		if (locked) {
			throw new IOException("Unable to write. This OutputStream is locked.");
		}
		super.write(b);
	}
	
	@Override
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}
}
