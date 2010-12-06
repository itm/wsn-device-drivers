package de.uniluebeck.itm.devicedriver.async.singlethread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Pausable single thread executor.
 * 
 * @author Malte Legenhausen
 */
public class PausableSingleThreadExecutor extends ThreadPoolExecutor implements PausableExecutorService {

	/**
	 * Flag for storing pause state.
	 */
	private boolean isPaused;
	
	/**
	 * Pause lock.
	 */
	private ReentrantLock pauseLock = new ReentrantLock();
	
	/**
	 * Condition to wait for unpause.
	 */
	private Condition unpaused = pauseLock.newCondition();

	/**
	 * Constructor.
	 */
	public PausableSingleThreadExecutor() {
		super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		pauseLock.lock();
		try {
			while (isPaused) {
				unpaused.await();
			}
		} catch (InterruptedException ie) {
			t.interrupt();
		} finally {
			pauseLock.unlock();
		}
	}

	@Override
	public void pause() {
		pauseLock.lock();
		try {
			isPaused = true;
		} finally {
			pauseLock.unlock();
		}
	}

	@Override
	public void resume() {
		pauseLock.lock();
		try {
			isPaused = false;
			unpaused.signalAll();
		} finally {
			pauseLock.unlock();
		}
	}
	
	@Override
	public boolean isPaused() {
		return isPaused;
	}
	
	@Override
	protected void finalize() {
		shutdown();
	}
}
