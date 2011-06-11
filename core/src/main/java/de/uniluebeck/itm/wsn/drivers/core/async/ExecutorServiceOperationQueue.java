package de.uniluebeck.itm.wsn.drivers.core.async;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.internal.Nullable;

import de.uniluebeck.itm.wsn.drivers.core.State;
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
public class ExecutorServiceOperationQueue implements OperationQueue {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ExecutorServiceOperationQueue.class);
	
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
	private final ExecutorService executor;
	
	/**
	 * Constructor.
	 */
	public ExecutorServiceOperationQueue() {
		this(Executors.newSingleThreadExecutor(
				new ThreadFactoryBuilder().setNameFormat("SingleThreadExecutor %d").build()));
	}
	
	/**
	 * Constructor.
	 * 
	 * @param executor Set a custom <code>PausableExecutorService</code>.
	 */
	public ExecutorServiceOperationQueue(final ExecutorService executor) {
		this.executor = executor;
	}
	
	/**
	 * Returns the executor used by this queue.
	 * 
	 * @return The executor for this queue.
	 */
	public ExecutorService getExecutorService() {
		return executor;
	}
	
	@Override
	public synchronized <T> OperationFuture<T> addOperation(Operation<T> operation, 
															long timeout, 
															@Nullable AsyncCallback<T> callback) {
		checkNotNull(operation, "Null Operation is not allowed.");
		checkArgument(timeout >= 0, "Negative timeout is not allowed.");
		
		operation.setAsyncCallback(callback);
		operation.setTimeout(timeout);
		
		// Add listener for removing operation.
		operation.addListener(new OperationListener<T>() {
			@Override
			public void onStateChanged(StateChangedEvent<T> event) {
				ExecutorServiceOperationQueue.this.onStateChanged(event);
			}
		});
		addOperation(operation);
		
		// Submit the operation to the executor.
		LOG.trace("Submit {} to executor.",  operation.getClass().getName());
		ListenableFuture<T> future = Futures.makeListenable(executor.submit(operation));
		return new SimpleOperationFuture<T>(future, operation);
	}
	
	/**
	 * Remove the operation from the internal list and fire the state change.
	 * 
	 * @param event
	 */
	private void onStateChanged(StateChangedEvent<?> event) {
		Operation<?> operation = event.getOperation();
		if (State.isFinishState(event.getNewState())) {
			removeOperation(operation);
		}
		fireStateChangedEvent(event);
	}
	
	/**
	 * Add the operation to the internal operation list.
	 * 
	 * @param <T> The return type of the operation.
	 * @param operation The operation that has to be added to the internal operation list.
	 */
	private <T> void addOperation(final Operation<T> operation) {
		operations.add(operation);
		LOG.trace("{} added to internal operation list", operation.getClass().getName());
		fireAddedEvent(new AddedEvent<T>(this, operation));
	}
	
	/**
	 * Remove the operation from the queue.
	 * 
	 * @param <T> Return type of the operation.
	 * @param operation The operation that has to be removed.
	 */
	private <T> void removeOperation(final Operation<T> operation) {
		operations.remove(operation);
		LOG.trace("{} removed from internal operation list", operation.getClass().getName());
		fireRemovedEvent(new RemovedEvent<T>(this, operation));
	}

	@Override
	public List<Operation<?>> getOperations() {
		return operations;
	}

	@Override
	public void addListener(final OperationQueueListener<?> listener) {
		checkNotNull(listener, "Null listener is not allowed.");
		listeners.add(listener);
	}

	@Override
	public void removeListener(final OperationQueueListener<?> listener) {
		checkNotNull(listener, "Null listener is not allowed.");
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
