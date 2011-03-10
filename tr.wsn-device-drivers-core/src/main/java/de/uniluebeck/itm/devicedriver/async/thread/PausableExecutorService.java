package de.uniluebeck.itm.devicedriver.async.thread;

import java.util.concurrent.ExecutorService;


/**
 * This Interface defindes a executor service which allows to pause and unpause further execution of <code>Runnable</code>s.
 * 
 * @author Malte Legenhausen
 */
public interface PausableExecutorService extends ExecutorService {

	/**
	 * Stop the execution of new <code>Runnable</code>s.
	 */
	void pause();
	
	/**
	 * Resume the execution of submitted <code>Runnable</code>s.
	 */
	void resume();
	
	/**
	 * Return the current pause state.
	 * 
	 * @return true if the executor service is paused else false.
	 */
	boolean isPaused();
}
