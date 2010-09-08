package de.uniluebeck.itm.devicedriver.async;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Operation;

/**
 * Container that manages a device operation.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The result type of the operation.
 */
public class OperationContainer<T> implements Callable<T> {

	/**
	 * Enum for all states of an <code>Operation</code>.
	 * 
	 * @author Malte Legenhausen
	 */
	public enum State {
		
		/**
		 * The <code>Operation</code> is waiting in the execution queue.
		 */
		WAITING("Waiting"),
		
		/**
		 * The <code>Operation</code> is currently running.
		 */
		RUNNING("Running"),
		
		/**
		 * The <code>Operation</code> has been canceled.
		 */
		CANCELED("Canceled"),
		
		/**
		 * The <code>Operation</code> finsihed with an exception.
		 */
		EXCEPTED("Excepted"),
		
		/**
		 * The <code>Operation</code> is done.
		 */
		DONE("Done");
		
		/**
		 * The name of the state.
		 */
		private final String name;
		
		/**
		 * Constructor.
		 * 
		 * @param name The name of the state.
		 */
		private State(String name) {
			this.name = name;
		}
		
		/**
		 * Getter for the state name.
		 * 
		 * @return The name as <code>String</code>.
		 */
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	/**
	 * Logger for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(OperationContainer.class);
	
	/**
	 * Listeners for <code>OperationContainer</code> changed.
	 */
	private final List<OperationContainerListener<T>> listeners = new ArrayList<OperationContainerListener<T>>();
	
	/**
	 * Associated operation with this container.
	 */
	private final Operation<T> operation;
	
	/**
	 * The timeout after which the application will be canceled.
	 */
	private final long timeout;
	
	/**
	 * The callback that is called when the operation has finished, canceled or when an exception occured.
	 */
	private final AsyncCallback<T> callback;
	
	/**
	 * The current state of the <code>Operation</code>.
	 */
	private State state = State.WAITING;
	
	/**
	 * <code>Timer</code> that executes the timeout operation.
	 */
	private final Timer timer = new Timer(); 
	
	/**
	 * Constructor.
	 * 
	 * @param operation The <code>Operation</code> associated with this container.
	 * @param timeout The timeout after which the operation has to be canceled.
	 * @param callback The callback method for the result.
	 */
	public OperationContainer(Operation<T> operation, long timeout, AsyncCallback<T> callback) {
		this.operation = operation;
		this.timeout = timeout;
		this.callback = callback;
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				logger.debug("Timeout of operation reached");
				State oldState = state;
				state = State.CANCELED;
				fireTimeout();
				logger.debug("Operation state changed from " + oldState + " to " + state);
				fireStateChanged(oldState, state);
			}
		}, timeout);
	}
	
	@Override
	public T call() {
		state = State.RUNNING;
		logger.debug("Operation state changed from " + State.WAITING + " to " + state);
		fireStateChanged(State.WAITING, state);
		try {
			final T result = operation.execute(callback);
			if (operation.isCanceled()) {
				state = State.CANCELED;
				callback.onCancel();
			} else {
				state = State.DONE;
				callback.onSuccess(result);
			}
			logger.debug("Operation state changed from " + State.RUNNING + " to " + state);
			fireStateChanged(State.RUNNING, state);
			return result;
		} catch (RuntimeException e) {
			state = State.EXCEPTED;
			callback.onFailure(e);
			logger.debug("Operation state changed from " + State.RUNNING + " to " + state);
			fireStateChanged(State.RUNNING, state);
		}
		return null;
	}
	
	/**
	 * Notify all listeners that the state has changed.
	 * 
	 * @param oldState The old state.
	 * @param newState The new state.
	 */
	private void fireStateChanged(State oldState, State newState) {
		for (OperationContainerListener<T> listener : listeners.toArray(new OperationContainerListener[listeners.size()])) {
			listener.onStateChanged(this, oldState, newState);
		}
	}
	
	/**
	 * Notify all listeners that a timeout occures.
	 */
	private void fireTimeout() {
		for (OperationContainerListener<T> listener : listeners.toArray(new OperationContainerListener[listeners.size()])) {
			listener.onTimeout(this, timeout);
		}
	}
	
	/**
	 * Returns the operation associated with this container.
	 * 
	 * @return The operation.
	 */
	public Operation<T> getOperation() {
		return operation;
	}

	/**
	 * Returns the state of the operation.
	 * 
	 * @return The operation state.
	 */
	public State getState() {
		return state;
	}

	/**
	 * Returns the timeout for this operation.
	 * 
	 * @return The timeout of the operation.
	 */
	public long getTimeout() {
		return timeout;
	}
	
	/**
	 * Adds an <code>OperationListener</code> to the listener list.
	 * 
	 * @param listener The <code>OperationListener</code> that has to be added.
	 */
	public void addOperationContainerListener(OperationContainerListener<T> listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes the given <code>OperationListener</code> from the listener list.
	 * 
	 * @param listener The <code>OperationListener</code> that has to be removed.
	 */
	public void removeOperationContainerListener(OperationContainerListener<T> listener) {
		listeners.remove(listener);
	}
}
