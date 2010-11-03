package de.uniluebeck.itm.devicedriver.async.singlethread;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.async.OperationQueueListener;
import de.uniluebeck.itm.devicedriver.operation.Operation;
import de.uniluebeck.itm.devicedriver.operation.OperationAdapter;
import de.uniluebeck.itm.devicedriver.operation.OperationListener;

/**
 * Class that implements the queue as single thread executor.
 * Only one <code>Operation</code> is executed at once.
 * 
 * @author Malte Legenhausen
 */
public class SingleThreadOperationQueue implements OperationQueue {
	
	/**
	 * Logger for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(SingleThreadOperationQueue.class);
	
	/**
	 * List that contains all listeners.
	 */
	private final List<OperationQueueListener> listeners = new ArrayList<OperationQueueListener>();
	
	/**
	 * Queue for all <code>OperationContainer</code> that are in progress.
	 */
	private final List<Operation<?>> operations = new LinkedList<Operation<?>>();
	
	/**
	 * The single thread executor that runs the <code>OperationContainer</code>.
	 */
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	@Override
	public synchronized <T> OperationHandle<T> addOperation(Operation<T> operation, long timeout, final AsyncCallback<T> callback) {
		operations.add(operation);
		notifyAdded(operation);
		operation.addListener(new OperationListener<T>() {
			@Override
			public void onStateChanged(Operation<T> operation, State oldState, State newState) {
				notifyStateChanged(operation, oldState, newState);
				if (newState.isFinishState()) {
					operations.remove(operation);
					notifyRemoved(operation);
				}
			}

			@Override
			public void onTimeout(Operation<T> operation, long timeout) {
				notifyTimeout(operation, timeout);
			}
		});
		logger.debug("Init operation " + operation);
		operation.init(timeout, callback);
		
		logger.debug("Submit " + operation + " to executor queue.");
		final Future<T> future = executor.submit(operation);
		operation.addListener(new OperationAdapter<T>() {
			@Override
			public void onTimeout(Operation<T> operation, long timeout) {
				future.cancel(true);
			}
		});
		// Start the timer to schedule.
		operation.scheduleTimeout();
		return new FutureOperationHandle<T>(future, operation);
	}

	@Override
	public List<Operation<?>> getOperations() {
		return operations;
	}

	@Override
	public void addListener(OperationQueueListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(OperationQueueListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Notify all listeners that the state of a operation has changed.
	 * 
	 * @param operation The operation that has changed the state.
	 * @param oldState The old state of the operation.
	 * @param newState The new state of the operation.
	 */
	private void notifyStateChanged(Operation<?> operation, State oldState, State newState) {
		for (OperationQueueListener listener : listeners.toArray(new OperationQueueListener[listeners.size()])) {
			listener.onStateChanged(operation, oldState, newState);
		}
	}
	
	/**
	 * Notify all listeners that a operation has reached his timeout.
	 * 
	 * @param operation The operation that timed out.
	 * @param timeout The timeout limit that was set for this operation.
	 */
	private void notifyTimeout(Operation<?> operation, long timeout) {
		for (OperationQueueListener listener : listeners.toArray(new OperationQueueListener[listeners.size()])) {
			listener.onTimeout(operation, timeout);
		}
	}
	
	/**
	 * Notify all listeners that a operation was added to the queue.
	 * 
	 * @param operation The added operation.
	 */
	private void notifyAdded(Operation<?> operation) {
		for (OperationQueueListener listener : listeners.toArray(new OperationQueueListener[listeners.size()])) {
			listener.onAdded(operation);
		}
	}
	
	/**
	 * Notify all listeners that a operation was removed from the queue.
	 * 
	 * @param operation The removed operation.
	 */
	private void notifyRemoved(Operation<?> operation) {
		for (OperationQueueListener listener : listeners.toArray(new OperationQueueListener[listeners.size()])) {
			listener.onRemoved(operation);
		}
	}
}
