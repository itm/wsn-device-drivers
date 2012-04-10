package de.uniluebeck.itm.wsn.drivers.core.operation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import de.uniluebeck.itm.wsn.drivers.core.util.ClassUtil;

/**
 * An abstract runnable.
 * If no other timeout is set the runnable will be canceled automatically after the <code>DEFAULT_TIMEOUT</code>.
 * The result of a timed out runnable is null also when the runnable completed at the same time.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type of the runnable.
 */
public class TimeLimitedOperation<T> implements Operation<T>, OperationContext {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(TimeLimitedOperation.class);
	
	/**
	 * Listeners for <code>OperationRunnable</code> changes.
	 */
	private final EventListenerSupport<OperationListener<T>> listeners = 
			EventListenerSupport.create(ClassUtil.<OperationListener<T>>castClass(OperationListener.class));
	
	private final OperationRunnable<T> runnable;
	
	/**
	 * Limiter for the execution time of an runnable.
	 */
	private final TimeLimiter timeLimiter;
	
	/**
	 * The timeout after which the application will be canceled.
	 */
	private final long timeout;
	
	/**
	 * The callback that is called when the runnable has finished, canceled or when an exception occured.
	 */
	private final OperationCallback<T> callback;
	
	/**
	 * ProgressManager used for tracking the progress of the <code>OperationRunnable</code>.
	 */
	private final ProgressManager progressManager;
	
	/**
	 * The current state of the <code>OperationRunnable</code>.
	 */
	private State state = State.WAITING;
	
	/**
	 * Boolean thats stores if the operatio has to be canceled.
	 */
	private boolean canceled = false;
	
	/**
	 * Constructor.
	 */
	@Inject
	public TimeLimitedOperation(TimeLimiter timeLimiter, ProgressManager progressManager, OperationRunnable<T> runnable,
								long timeout, OperationCallback<T> callback) {
		this.timeLimiter = timeLimiter;
		this.progressManager = progressManager;
		this.runnable = runnable;
		this.timeout = timeout;
		this.callback = callback;
	}
	
	@Override
	public T call() throws Exception {
		setState(State.RUNNING);
		
		callback.onExecute();
		T result = null;
		try {
			// Cancel execution if runnable was canceled before runnable changed to running.
			if (!canceled) {
				result = executeOperation();
			}
		} catch (UncheckedTimeoutException e) {
			setState(State.TIMEDOUT);
			LOG.error("Timeout reached during operation execution", e);
			TimeoutException timeoutException = new TimeoutException("Operation timeout " + timeout + "ms reached.");
			callback.onFailure(timeoutException);
			throw timeoutException;
		} catch (Exception e) {
			setState(State.EXCEPTED);
			LOG.error("Exception during operation execution", e);
			callback.onFailure(e);
			throw e;
		}	
		
		if (canceled) {
			setState(State.CANCELED);
			callback.onCancel();
			result = null;
		} else {
			setState(State.DONE);
			callback.onSuccess(result);
		}
		return result;
	}
	
	private T executeOperation() throws Exception {
		progressManager.worked(0.0f);
		final Callable<T> callable = new Callable<T>() {
			@Override
			public T call() throws Exception {
				return runnable.run(progressManager, TimeLimitedOperation.this);
			}
		};
		T result = timeLimiter.callWithTimeout(callable, timeout, TimeUnit.MILLISECONDS, false);
		progressManager.done();
		return result;
	}
	
	@Override
	public <R> R run(OperationRunnable<R> subRunnable, ProgressManager aProgressManager) throws Exception {
		checkNotNull(subRunnable, "Null operations are not allowed");
		checkNotNull(aProgressManager, "Null ProgressManager is not allowed.");
		final R result = subRunnable.run(aProgressManager, this);
		aProgressManager.done();
		return result;
	}
	
	@Override
	public <R> R run(OperationRunnable<R> subRunnable, ProgressManager aProgressManager, float subFraction) 
			throws Exception {
		checkNotNull(subRunnable, "Null operations are not allowed");
		checkNotNull(aProgressManager, "Null ProgressManager is not allowed.");
		ProgressManager subProgressManager = aProgressManager.createSub(subFraction);
		final R result = subRunnable.run(subProgressManager, this);
		subProgressManager.done();
		return result;
	}
	
	/**
	 * Thread safe state change function.
	 * 
	 * @param newState The new State of this runnable.
	 */
	private void setState(State newState) {
		synchronized (state) {
			State oldState = state;
			fireBeforeStateChangedEvent(new StateChangedEvent<T>(this, oldState, newState));
			state = newState;
			fireAfterStateChangedEvent(new StateChangedEvent<T>(this, oldState, newState));
		}
	}
	
	private void fireBeforeStateChangedEvent(StateChangedEvent<T> event) {
		String msg = "{} state changing from {} to {}";
		LOG.trace(msg, new Object[] {runnable.getClass().getName(), event.getOldState(), event.getNewState()});
		listeners.fire().beforeStateChanged(event);
	}
	
	/**
	 * Notify all listeners that the state has changed.
	 * 
	 * @param event The state change event.
	 */
	private void fireAfterStateChangedEvent(StateChangedEvent<T> event) {
		String msg = "{} state changed from {} to {}";
		LOG.trace(msg, new Object[] {runnable.getClass().getName(), event.getOldState(), event.getNewState()});
		listeners.fire().afterStateChanged(event);
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
		listeners.addListener(listener);
	}
	
	@Override
	public void removeListener(OperationListener<T> listener) {
		listeners.removeListener(listener);
	}

	@Override
	public void cancel() {
		canceled = true;
	}
	
	@Override
	public boolean isCanceled() {
		return canceled;
	}
}
