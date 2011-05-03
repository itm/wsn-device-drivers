package de.uniluebeck.itm.wsn.drivers.core.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * <code>FilterInputStream</code> implementation that can be locked.
 * When locked you are not able to read from the stream and the <code>available</code> method will always return 0.
 * For locking use the <code>setLocked</code> method.
 * 
 * @author Malte Legenhausen
 */
public class LockedInputStream extends FilterInputStream implements Lockable {
	
	/**
	 * The lock flag.
	 */
	private boolean locked;
	
	/**
	 * Constructor.
	 * 
	 * @param in The wrapped <code>InputStream</code>.
	 */
	public LockedInputStream(final InputStream in) {
		super(in);
	}
	
	@Override
	public int read() throws IOException {
		return locked ? -1 : super.read();
	}
	
	@Override
	public int available() throws IOException {
		return locked ? 0 : super.available();
	}
	
	@Override
	public void setLocked(final boolean locked) {
		this.locked = locked;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

}
