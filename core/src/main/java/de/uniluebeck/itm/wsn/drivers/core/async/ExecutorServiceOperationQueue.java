package de.uniluebeck.itm.wsn.drivers.core.async;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.uniluebeck.itm.wsn.drivers.core.event.AddedEvent;
import de.uniluebeck.itm.wsn.drivers.core.event.RemovedEvent;
import de.uniluebeck.itm.wsn.drivers.core.event.StateChangedEvent;
import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;
import de.uniluebeck.itm.wsn.drivers.core.operation.OperationAdapter;

/**
 * Class that implements the queue with the single thread executor from the Java Concurrency Framework. Only one
 * <code>Operation</code> is executed at once.
 *
 * @author Malte Legenhausen
 */
@Singleton
public class ExecutorServiceOperationQueue implements OperationQueue {

	private class OperationFinishedRunnable implements Runnable {
		
		private final Operation<?> operation;
		
		public OperationFinishedRunnable(Operation<?> operation) {
			this.operation = operation;
		}
		
		@Override
		public void run() {
			operationFinished(operation);
		}
	}
	
	/**
	 * Logger for this class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ExecutorServiceOperationQueue.class);

	/**
	 * List that contains all listeners.
	 */
	private final List<OperationQueueListener<?>> listeners = newArrayList();

	/**
	 * Queue for all <code>OperationContainer</code> that are in progress.
	 */
	private final List<Operation<?>> operations = Collections.synchronizedList(new LinkedList<Operation<?>>());
	
	private final Map<Operation<?>, Runnable> runnables = newHashMap();

	/**
	 * The single thread executor that runs the <code>OperationContainer</code>.
	 */
	private final ExecutorService executor;
	
	private final TimeLimiter timeLimiter;

	/**
	 * Constructor.
	 */
	public ExecutorServiceOperationQueue() {
		this(Executors.newSingleThreadExecutor(
				new ThreadFactoryBuilder().setNameFormat("OperationQueue-Thread %d").build()
			), new SimpleTimeLimiter()
		);
	}

	/**
	 * Constructor.
	 *
	 * @param executor Set a custom <code>PausableExecutorService</code>.
	 */
	@Inject
	public ExecutorServiceOperationQueue(ExecutorService executor, TimeLimiter timeLimiter) {
		this.executor = executor;
		this.timeLimiter = timeLimiter;
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
	public <T> OperationFuture<T> addOperation(final Operation<T> operation, 
											   long timeout, 
											   @Nullable AsyncCallback<T> callback) {
		
		checkNotNull(operation, "Null Operation is not allowed.");
		checkArgument(timeout >= 0, "Negative timeout is not allowed.");

		operation.setAsyncCallback(callback);
		operation.setTimeout(timeout);
		operation.setTimeLimiter(timeLimiter);

		// Add listener for removing operation.
		operation.addListener(new OperationAdapter<T>() {
			@Override
			public void afterStateChanged(StateChangedEvent<T> event) {
				fireStateChangedEvent(event);
			}
		});

		final ListenableFutureTask<T> future = new ListenableFutureTask<T>(operation);
		synchronized (operations) {
			addOperation(operation, future);
			executeNext();
		}
		future.addListener(new OperationFinishedRunnable(operation), executor);
		return new SimpleOperationFuture<T>(future, operation);
	}
	
	private void operationFinished(Operation<?> operation) {
		synchronized (operations) {
			removeOperation(operation);
			executeNext();
		}
	}
	
	private void executeNext() {
		if (!operations.isEmpty()) {
			Operation<?> operation = operations.get(0);
			Runnable runnable = runnables.get(operation);
			LOG.trace("Submit {} to executor.", operation.getClass().getName());
			executor.execute(runnable);
		}
	}

	/**
	 * Add the operation to the internal operation list.
	 *
	 * @param <T>       The return type of the operation.
	 * @param operation The operation that has to be added to the internal operation list.
	 */
	private <T> void addOperation(Operation<T> operation, Runnable runnable) {
		operations.add(operation);
		runnables.put(operation, runnable);
		LOG.trace("{} added to internal operation list", operation.getClass().getName());
		fireAddedEvent(new AddedEvent<T>(this, operation));
	}

	/**
	 * Remove the operation from the queue.
	 *
	 * @param <T>       Return type of the operation.
	 * @param operation The operation that has to be removed.
	 */
	private <T> void removeOperation(Operation<T> operation) {
		operations.remove(operation);
		runnables.remove(operation);
		LOG.trace("{} removed from internal operation list", operation.getClass().getName());
		fireRemovedEvent(new RemovedEvent<T>(this, operation));
	}

	@Override
	public List<Operation<?>> getOperations() {
		return newArrayList(operations);
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

	/**
	 * Notify all listeners that the state of a operation has changed.
	 *
	 * @param <T>   The type of the operation.
	 * @param event The event that will notify about the state change.
	 */
	private <T> void fireStateChangedEvent(final StateChangedEvent<T> event) {
		String msg = "Operation state of {} changed";
		LOG.trace(msg, new Object[]{event.getOperation().getClass().getName()});
		for (final OperationQueueListener<T> listener : listeners.toArray(new OperationQueueListener[0])) {
			listener.afterStateChanged(event);
		}
	}

	/**
	 * Notify all listeners that a operation was added to the queue.
	 *
	 * @param <T>   The type of the operation.
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
	 * @param <T>   The type of the operation.
	 * @param event The event that will notify about the remove of an operation.
	 */
	private <T> void fireRemovedEvent(final RemovedEvent<T> event) {
		for (final OperationQueueListener<T> listener : listeners.toArray(new OperationQueueListener[0])) {
			listener.onRemoved(event);
		}
	}
}
