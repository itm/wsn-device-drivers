package de.uniluebeck.itm.devicedriver.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.exception.TimeoutException;

/**
 * An abstract operation.
 * The result of a timed out operation is null also when the operation completed at the same time.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type of the operation.
 */
public abstract class AbstractOperation<T> implements Operation<T> {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Operation.class);
	
	/**
	 * Sub <code>Operation</code> that is currently running.
	 */
	private Operation<?> subOperation;
	
	/**
	 * Listeners for <code>OperationContainer</code> changed.
	 */
	private final List<OperationListener<T>> listeners = new ArrayList<OperationListener<T>>();
	
	/**
	 * The timeout after which the application will be canceled.
	 */
	private long timeout;
	
	/**
	 * The callback that is called when the operation has finished, canceled or when an exception occured.
	 */
	private AsyncCallback<T> callback;
	
	/**
	 * The current state of the <code>Operation</code>.
	 */
	private State state = State.WAITING;
	
	/**
	 * <code>Timer</code> that executes the timeout operation.
	 */
	private final Timer timer = new Timer(getClass().getName());
	
	/**
	 * Boolean thats stores if the operatio has to be canceled.
	 */
	private boolean canceled;
	
	/**
	 * Method is called when the timeout occured.
	 */
	protected void onTimeout() {
		changeState(State.TIMEDOUT);
		callback.onFailure(new TimeoutException("Operation timeout " + timeout + "ms reached."));
	}
	
	@Override
	public void init(final long timeout, final AsyncCallback<T> callback) {
		this.timeout = timeout;
		this.callback = callback;
		
		final TimerTask task = new TimerTask() {			
			@Override
			public void run() {
				onTimeout();
			}
		};
		timer.schedule(task, timeout);
	}
	
	@Override
	public T call() {
		changeState(State.RUNNING);
		try {
			final T result = execute(callback);
			timer.cancel();
			
			// Do nothing after a timeout happens and execute finished.
			synchronized (state) {
				if (state.equals(State.TIMEDOUT)) {
					logger.warn("Operation finsihed but timeout occured.");
					return null;
				}
			}
			
			if (canceled) {
				changeState(State.CANCELED);
				callback.onCancel();
			} else {
				changeState(State.DONE);
				callback.onSuccess(result);
			}
			return result;
		} catch (Exception e) {
			changeState(State.EXCEPTED);
			callback.onFailure(e);
		}
		return null;
	}
	
	/**
	 * Call this method when another <code>Operation</code> has to be executed while this <code>Operation</code>.
	 * 
	 * @param <R> The return type of the sub <code>Operation</code>.
	 * @param operation The sub <code>Operation</code> that has to be executed.
	 * @return The result of the sub <code>Operation</code>.
	 */
	protected <R> R executeSubOperation(Operation<R> operation) throws Exception {
		subOperation = operation;
		final R result = operation.execute(callback);
		subOperation = null;
		return result;
	}
	
	/**
	 * Thread safe state change function.
	 * 
	 * @param newState The new State of this operation.
	 */
	private void changeState(State newState) {
		synchronized (state) {
			final State oldState = state;
			state = newState;
			fireStateChanged(oldState, newState);
		}
	}
	
	/**
	 * Notify all listeners that the state has changed.
	 * 
	 * @param oldState The old state.
	 * @param newState The new state.
	 */
	private void fireStateChanged(State oldState, State newState) {
		logger.debug("Operation state changed from " + oldState + " to " + newState);
		for (OperationListener<T> listener : listeners.toArray(new OperationListener[listeners.size()])) {
			listener.onStateChanged(this, oldState, newState);
		}
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public long getTimeout() {
		return timeout;
	}
	
	@Override
	public void addListener(OperationListener<T> listener) {
		listeners.add(listener);
	}
	
	@Override
	public void removeOperationListener(OperationListener<T> listener) {
		listeners.remove(listener);
	}

	@Override
	public void cancel() {
		if (subOperation != null) {
			subOperation.cancel();
		}
		canceled = true;
	}
	
	@Override
	public boolean isCanceled() {
		return canceled;
	}
	
	@Override
	protected void finalize() {
		timer.cancel();
	}
}
