package de.uniluebeck.itm.wsn.drivers.core.async;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

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
	private final ReentrantLock pauseLock = new ReentrantLock();

	/**
	 * Condition to wait for unpause.
	 */
	private final Condition unpaused = pauseLock.newCondition();

	/**
	 * Constructor.
	 */
	public PausableSingleThreadExecutor() {
		super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
				new ThreadFactoryBuilder().setNameFormat("PausableSingleThreadExecutor %d").build()
		);
	}

	@Override
	protected void beforeExecute(final Thread thread, final Runnable runnable) {
		super.beforeExecute(thread, runnable);
		pauseLock.lock();
		try {
			while (isPaused) {
				unpaused.await();
			}
		} catch (final InterruptedException e) {
			thread.interrupt();
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
