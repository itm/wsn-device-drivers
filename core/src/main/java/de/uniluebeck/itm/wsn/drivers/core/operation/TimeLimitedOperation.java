package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import de.uniluebeck.itm.wsn.drivers.core.util.ClassUtil;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An abstract base class for {@link Operation} implementations.
 *
 * @param <ResultType>
 * 		The return type of the operation.
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public abstract class TimeLimitedOperation<ResultType> implements Operation<ResultType> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Listeners for <code>OperationRunnable</code> changes.
	 */
	protected final EventListenerSupport<OperationListener<ResultType>> listeners =
			EventListenerSupport.create(ClassUtil.<OperationListener<ResultType>>castClass(OperationListener.class));

	/**
	 * Limiter for the execution time of an runnable.
	 */
	protected final TimeLimiter timeLimiter;

	/**
	 * The timeout after which the application will be canceled.
	 */
	protected final long timeoutMillis;

	/**
	 * A lock for controlling concurrent access to {@link TimeLimitedOperation#state}.
	 */
	protected final Lock stateLock = new ReentrantLock();

	/**
	 * The current state of the <code>OperationRunnable</code>.
	 */
	protected State state = State.WAITING;

	/**
	 * A condition that becomes true as soon as the operation is done.
	 */
	protected final Condition operationDone = stateLock.newCondition();

	/**
	 * Boolean that stores if the operation has to be canceled.
	 */
	protected boolean canceled = false;

	private float progress = 0f;

	public TimeLimitedOperation(final TimeLimiter timeLimiter, final long timeoutMillis,
								@Nullable final OperationListener<ResultType> listener) {

		checkNotNull(timeLimiter);
		checkArgument(timeoutMillis > 0, "Timeout must be larger than larger than zero milliseconds!");

		this.timeLimiter = timeLimiter;
		this.timeoutMillis = timeoutMillis;

		if (listener != null) {
			this.listeners.addListener(listener);
		}
	}

	@Override
	public void cancel() {
		canceled = true;
		stateLock.lock();
		try {
			operationDone.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			stateLock.unlock();
		}
	}

	@Override
	public final ResultType call() throws Exception {

		setState(State.RUNNING);
		listeners.fire().onExecute();
		ResultType result = null;

		try {

			// Cancel execution if runnable was canceled before runnable changed to running.
			if (!canceled) {
				progress(0f);
				log.trace("Running {} operation with {} ms timeout", this.getClass().getSimpleName(), timeoutMillis);
				result = timeLimiter.callWithTimeout(new Callable<ResultType>() {
														 @Override
														 public ResultType call() throws Exception {
															 return callInternal();
														 }
													 }, timeoutMillis, TimeUnit.MILLISECONDS, false
				);
				progress(1f);
			}

		} catch (UncheckedTimeoutException e) {

			setState(State.TIMEOUT);
			TimeoutException timeoutException =
					new TimeoutException("Operation timed out after " + timeoutMillis + " ms");
			listeners.fire().onFailure(timeoutException);
			throw timeoutException;

		} catch (Exception e) {

			setState(State.FAILED);
			listeners.fire().onFailure(e);
			throw e;
		}

		if (canceled) {

			setState(State.CANCELED);
			listeners.fire().onCancel();
			result = null;

		} else {

			setState(State.DONE);
			listeners.fire().onSuccess(result);
		}

		return result;
	}

	/**
	 * All operation execution code goes here. This method is call by {@link de.uniluebeck.itm.wsn.drivers.core.operation.TimeLimitedOperation#call()}
	 * which manages the operation state and notifies listeners about operation start and end.
	 *
	 * @return the result of the operation
	 *
	 * @throws Exception
	 * 		if an arbitrary exception occurs
	 */
	protected abstract ResultType callInternal() throws Exception;

	@Override
	public State getState() {
		return state;
	}

	@Override
	public long getTimeoutMillis() {
		return timeoutMillis;
	}

	@Override
	public void addListener(OperationListener<ResultType> listener) {
		listeners.addListener(listener);
	}

	@Override
	public void removeListener(OperationListener<ResultType> listener) {
		listeners.removeListener(listener);
	}

	protected boolean isCanceled() {
		return canceled;
	}

	protected <R> R runSubOperation(final Operation<R> subOperation, final float subFraction) throws Exception {
		checkNotNull(subOperation, "Null operations are not allowed");
		subOperation.addListener(new OperationAdapter<R>() {

									 private final float initialParentOperationProgress =
											 TimeLimitedOperation.this.progress;

									 @Override
									 public void onProgressChange(final float fraction) {
										 log.trace(
												 "Operation {}, progress: {}, suboperation {}, suboperation progress: {}",
												 TimeLimitedOperation.this.getClass().getSimpleName(),
												 TimeLimitedOperation.this.progress,
												 subOperation.getClass().getSimpleName(),
												 fraction
										 );
										 progress(initialParentOperationProgress + subFraction * fraction);
									 }
								 }
		);
		return subOperation.call();
	}

	/**
	 * Use this method to set the progress of work that was already done.
	 * The amount of work starts at 0.0f and goes up to 1.0f.
	 *
	 * @param progress
	 * 		The progress amount.
	 */
	protected void progress(float progress) {

		log.trace("{} progress (old={}, new={})",
				this.getClass().getSimpleName(), this.progress, progress
		);

		checkArgument(progress >= this.progress,
				"A new progress value (%s) must be larger than the old value (%s). "
						+ "It wouldn't be a progress otherwise, would it?", progress, this.progress
		);
		checkArgument(progress >= 0f && progress <= 1f, "Progress must be between zero and one (is %s).", progress);

		this.progress = progress;
		this.listeners.fire().onProgressChange(progress);
	}

	/**
	 * Thread safe state change function.
	 *
	 * @param newState
	 * 		The new State of this runnable.
	 */
	private void setState(State newState) {
		stateLock.lock();
		try {
			State oldState = state;
			fireBeforeStateChangedEvent(new StateChangedEvent<ResultType>(this, oldState, newState));
			state = newState;
			if (State.isFinishState(state)) {
				operationDone.signalAll();
			}
			fireAfterStateChangedEvent(new StateChangedEvent<ResultType>(this, oldState, newState));
		} finally {
			stateLock.unlock();
		}
	}

	private void fireBeforeStateChangedEvent(StateChangedEvent<ResultType> event) {
		String msg = "{} state changing from {} to {}";
		log.trace(msg, this.getClass().getSimpleName(), event.getOldState(), event.getNewState());
		listeners.fire().beforeStateChanged(event);
	}

	/**
	 * Notify all listeners that the state has changed.
	 *
	 * @param event
	 * 		The state change event.
	 */
	private void fireAfterStateChangedEvent(StateChangedEvent<ResultType> event) {
		String msg = "{} state changed from {} to {}";
		log.trace(msg, this.getClass().getSimpleName(), event.getOldState(), event.getNewState());
		listeners.fire().afterStateChanged(event);
	}
}
