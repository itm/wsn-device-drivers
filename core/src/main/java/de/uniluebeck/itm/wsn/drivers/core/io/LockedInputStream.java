package de.uniluebeck.itm.wsn.drivers.core.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * <code>FilterInputStream</code> implementation that can be locked.
 * When locked you are not able to read from the stream and the <code>available</code> method will always return 0.
 * For locking use the <code>setLocked</code> method.
 * 
 * @author Malte Legenhausen
 */
public class LockedInputStream extends FilterInputStream implements Lockable {
	
	/**
	 * Lock for signaling a lock change.
	 */
	private final Lock lock = new ReentrantLock();
	
	/**
	 * The condition for waiting for a lock change.
	 */
	private final Condition isLocked = lock.newCondition();
	
	/**
	 * The lock flag.
	 */
	private boolean locked = false;
	
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
		if (locked) {
			lock.lock();
			try {
				isLocked.awaitUninterruptibly();
			} finally {
				lock.unlock();
			}
		}
		return super.read();
	}
	
	@Override
	public int available() throws IOException {
		return locked ? 0 : super.available();
	}
	
	@Override
	public long skip(long n) throws IOException {
		if (locked) {
			throw new IOException("Unable to skip. Stream is currently locked.");
		}
		return super.skip(n);
	}
	
	@Override
	public void close() throws IOException {
		if (locked) {
			throw new IOException("Unable to close. Stream is currently locked.");
		}
		super.close();
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		if (!locked) {
			super.mark(readlimit);
		}
	}
	
	@Override
	public synchronized void reset() throws IOException {
		if (locked) {
			throw new IOException("Unable to reset. Stream is currently locked.");
		}
		super.reset();
	}
	
	@Override
	public void setLocked(final boolean locked) {
		this.locked = locked;
		if (!locked) {
			lock.lock();
			try {
				isLocked.signal();
			} finally {
				lock.unlock();
			}
		}
	}

	@Override
	public boolean isLocked() {
		return locked;
	}
}
