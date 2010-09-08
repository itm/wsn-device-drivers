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
	public synchronized <T> OperationHandle<T> addOperation(Operation<T> operation, long timeout, AsyncCallback<T> callback) {
		operations.add(operation);
		operation.addOperationListener(new OperationAdapter<T>() {
			@Override
			public void onStateChanged(Operation<T> operation, State oldState, State newState) {
				if (newState == State.DONE || newState == State.EXCEPTED || newState == State.CANCELED) {
					operations.remove(operation);
				}
			}
		});
		logger.debug("Init operation " + operation);
		operation.init(timeout, callback);
		logger.debug("Submit " + operation + " to executor queue.");
		final Future<T> future = executor.submit(operation);
		operation.addOperationListener(new OperationAdapter<T>() {
			@Override
			public void onTimeout(Operation<T> operation, long timeout) {
				future.cancel(true);
			}
		});
		return new FutureOperationHandle<T>(future, operation);
	}

	@Override
	public List<Operation<?>> getOperations() {
		return operations;
	}

	@Override
	public void addOperationQueueListener(OperationQueueListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeOperationQueueListener(OperationQueueListener listener) {
		listeners.remove(listener);
	}
}
