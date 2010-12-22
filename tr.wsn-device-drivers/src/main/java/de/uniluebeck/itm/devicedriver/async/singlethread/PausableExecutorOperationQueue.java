package de.uniluebeck.itm.devicedriver.async.singlethread;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.async.OperationQueueListener;
import de.uniluebeck.itm.devicedriver.event.AddedEvent;
import de.uniluebeck.itm.devicedriver.event.RemovedEvent;
import de.uniluebeck.itm.devicedriver.event.StateChangedEvent;
import de.uniluebeck.itm.devicedriver.operation.Operation;
import de.uniluebeck.itm.devicedriver.operation.OperationListener;

/**
 * Class that implements the queue with the single thread executor from the Java Concurrency Framework.
 * Only one <code>Operation</code> is executed at once.
 * 
 * @author Malte Legenhausen
 */
public class PausableExecutorOperationQueue implements OperationQueue {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(PausableExecutorOperationQueue.class);
	
	/**
	 * List that contains all listeners.
	 */
	private final List<OperationQueueListener<?>> listeners = new ArrayList<OperationQueueListener<?>>();
	
	/**
	 * Queue for all <code>OperationContainer</code> that are in progress.
	 */
	private final List<Operation<?>> operations = new LinkedList<Operation<?>>();
	
	/**
	 * The single thread executor that runs the <code>OperationContainer</code>.
	 */
	private final PausableExecutorService executor;
	
	/**
	 * Constructor.
	 */
	public PausableExecutorOperationQueue() {
		this(new PausableSingleThreadExecutor());
	}
	
	/**
	 * Constructor.
	 * 
	 * @param executor Set a custom <code>PausableExecutorService</code>.
	 */
	public PausableExecutorOperationQueue(final PausableExecutorService executor) {
		this.executor = executor;
	}
	
	/**
	 * Returns the executor used by this queue.
	 * 
	 * @return The executor for this queue.
	 */
	public PausableExecutorService getExecutorService() {
		return executor;
	}
	
	@Override
	public synchronized <T> OperationHandle<T> addOperation(final Operation<T> operation, final long timeout, final AsyncCallback<T> callback) {
		operation.setAsyncCallback(callback);
		operation.setTimeout(timeout);
		
		// Add listener for removing operation.
		operation.addListener(new OperationListener<T>() {
			@Override
			public void onStateChanged(StateChangedEvent<T> event) {
				fireStateChangedEvent(event);
				if (event.getNewState().isFinishState()) {
					removeOperation(event.getOperation());
				}
			}
		});
		
		// Pause the executor to submit the operation and savely add the timeout handler.
		logger.debug("Pause executor");
		executor.pause();
		
		// Submit the operation to the executor.
		logger.debug("Submit " + operation + " to executor queue.");
		final Future<T> future = executor.submit(operation);
		
		// Add listener for timeout handling.
		operation.addListener(new OperationListener<T>() {
			@Override
			public void onStateChanged(StateChangedEvent<T> event) {
				if (event.getNewState().equals(State.TIMEDOUT)) {
					cancelOperation(event.getOperation(), future);
				}
			}
		});
		
		addOperation(operation);
		
		// Resume executor to execute new submitted operations.
		logger.debug("Resume executor");
		executor.resume();
		
		return new FutureOperationHandle<T>(future, operation);
	}
	
	/**
	 * Add the operation to the internal operation list.
	 * 
	 * @param <T> The return type of the operation.
	 * @param operation The operation that has to be added to the internal operation list.
	 */
	private <T> void addOperation(final Operation<T> operation) {
		operations.add(operation);
		logger.debug("Operation added to internal operation list");
		fireAddedEvent(new AddedEvent<T>(this, operation));
	}
	
	/**
	 * Cancel the operation when the state changed to timeout.
	 * 
	 * @param <T> Return type of the operation and the future object.
	 * @param operation The operation that reached the timeout.
	 * @param future The future object to force the operation cancel.
	 */
	private <T> void cancelOperation(final Operation<T> operation, final Future<T> future) {
		final long timeout = operation.getTimeout();
		logger.warn("Operation " + operation + " will be canceled cause timeout of " + timeout + "ms was reached");
		future.cancel(true);
	}
	
	/**
	 * Remove the operation from the queue.
	 * 
	 * @param <T> Return type of the operation.
	 * @param operation The operation that has to be removed.
	 */
	private <T> void removeOperation(final Operation<T> operation) {
		operations.remove(operation);
		logger.debug("Operation removed from internal operation list");
		fireRemovedEvent(new RemovedEvent<T>(this, operation));
	}

	@Override
	public List<Operation<?>> getOperations() {
		return operations;
	}

	@Override
	public void addListener(final OperationQueueListener<?> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(final OperationQueueListener<?> listener) {
		listeners.remove(listener);
	}
	
	@Override
	public List<Operation<?>> shutdown(final boolean force) {
		if (force) {
			executor.shutdownNow();
		} else {
			executor.shutdown();
		}
		return operations;
	}
	
	/**
	 * Notify all listeners that the state of a operation has changed.
	 * 
	 * @param operation The operation that has changed the state.
	 * @param oldState The old state of the operation.
	 * @param newState The new state of the operation.
	 */
	private <T> void fireStateChangedEvent(final StateChangedEvent<T> event) {
		for (final OperationQueueListener<T> listener : listeners.toArray(new OperationQueueListener[listeners.size()])) {
			listener.onStateChanged(event);
		}
	}
	
	/**
	 * Notify all listeners that a operation was added to the queue.
	 * 
	 * @param operation The added operation.
	 */
	private <T> void fireAddedEvent(final AddedEvent<T> event) {
		for (final OperationQueueListener<T> listener : listeners.toArray(new OperationQueueListener[listeners.size()])) {
			listener.onAdded(event);
		}
	}
	
	/**
	 * Notify all listeners that a operation was removed from the queue.
	 * 
	 * @param operation The removed operation.
	 */
	private <T> void fireRemovedEvent(final RemovedEvent<T> event) {
		for (final OperationQueueListener<T> listener : listeners.toArray(new OperationQueueListener[listeners.size()])) {
			listener.onRemoved(event);
		}
	}
}
