package de.uniluebeck.itm.wsn.drivers.core.async.thread;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import de.uniluebeck.itm.wsn.drivers.core.State;
import de.uniluebeck.itm.wsn.drivers.core.async.AsyncCallback;
import de.uniluebeck.itm.wsn.drivers.core.async.OperationFuture;
import de.uniluebeck.itm.wsn.drivers.core.async.OperationQueue;
import de.uniluebeck.itm.wsn.drivers.core.async.OperationQueueListener;
import de.uniluebeck.itm.wsn.drivers.core.event.AddedEvent;
import de.uniluebeck.itm.wsn.drivers.core.event.RemovedEvent;
import de.uniluebeck.itm.wsn.drivers.core.event.StateChangedEvent;
import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationListener;

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
	private static final Logger LOG = LoggerFactory.getLogger(PausableExecutorOperationQueue.class);
	
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
	public synchronized <T> OperationFuture<T> addOperation(Operation<T> operation, 
															long timeout, 
															AsyncCallback<T> callback) {
		operation.setAsyncCallback(callback);
		operation.setTimeout(timeout);
		
		// Pause the executor to submit the operation and savely add the timeout handler.
		LOG.trace("Pause executor");
		executor.pause();
		
		// Submit the operation to the executor.
		LOG.trace("Submit " + operation + " to executor queue.");
		final ListenableFuture<T> future = Futures.makeListenable(executor.submit(operation));
		
		// Add listener for timeout handling and removing operation.
		operation.addListener(new OperationListener<T>() {
			@Override
			public void onStateChanged(final StateChangedEvent<T> event) {
				final Operation<T> operation = event.getOperation();
				if (State.TIMEDOUT.equals(event.getNewState())) {
					cancelOperation(operation, future);
				}
				if (State.isFinishState(event.getNewState())) {
					removeOperation(operation);
				}
				fireStateChangedEvent(event);
			}
		});
		
		addOperation(operation);
		
		// Resume executor to execute new submitted operations.
		LOG.trace("Resume executor");
		executor.resume();
		
		return new SimpleOperationFuture<T>(future, operation);
	}
	
	/**
	 * Add the operation to the internal operation list.
	 * 
	 * @param <T> The return type of the operation.
	 * @param operation The operation that has to be added to the internal operation list.
	 */
	private <T> void addOperation(final Operation<T> operation) {
		operations.add(operation);
		LOG.debug("Operation added to internal operation list");
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
		LOG.warn("Operation " + operation + " will be canceled cause timeout of " + timeout + "ms was reached");
		// Try to cancel in a normal way.
		operation.cancel();
		// Now kill the thread hard.
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
		LOG.trace("Operation removed from internal operation list");
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
	 * @param <T> The type of the operation.
	 * @param event The event that will notify about the state change.
	 */
	private <T> void fireStateChangedEvent(final StateChangedEvent<T> event) {
		String msg = "Operation state of {} changed";
		LOG.trace(msg, new Object[] {event.getOperation().getClass().getName()});
		for (final OperationQueueListener<T> listener : listeners.toArray(new OperationQueueListener[0])) {
			listener.onStateChanged(event);
		}
	}
	
	/**
	 * Notify all listeners that a operation was added to the queue.
	 * 
	 * @param <T> The type of the operation.
	 * @param event The event that will notify about the add of an operation.
	 */
	private <T> void fireAddedEvent(final AddedEvent<T> event) {
		for (final OperationQueueListener<T> listener : listeners.toArray(new OperationQueueListener[0])) {
			listener.onAdded(event);
		}
	}
	
	/**
	 * Notify all listeners that a operation was removed from the queue.
	 * 
	 * @param <T> THe type of the operation.
	 * @param event The event that will notify about the remove of an operation.
	 */
	private <T> void fireRemovedEvent(final RemovedEvent<T> event) {
		for (final OperationQueueListener<T> listener : listeners.toArray(new OperationQueueListener[0])) {
			listener.onRemoved(event);
		}
	}
}
