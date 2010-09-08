package de.uniluebeck.itm.devicedriver.async.singlethread;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.devicedriver.Operation;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationContainer;
import de.uniluebeck.itm.devicedriver.async.OperationContainerAdapter;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.async.OperationQueueListener;
import de.uniluebeck.itm.devicedriver.async.OperationContainer.State;

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
	private final List<OperationContainer<?>> operations = new LinkedList<OperationContainer<?>>();
	
	/**
	 * The single thread executor that runs the <code>OperationContainer</code>.
	 */
	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	@Override
	public synchronized <T> OperationHandle<T> addOperation(Operation<T> operation, long timeout, AsyncCallback<T> callback) {
		OperationContainer<T> container = new OperationContainer<T>(operation, timeout, callback);
		operations.add(container);
		container.addOperationContainerListener(new OperationContainerAdapter<T>() {
			@Override
			public void onStateChanged(OperationContainer<T> container, State oldState, State newState) {
				if (newState == State.DONE || newState == State.EXCEPTED || newState == State.CANCELED) {
					operations.remove(container);
				}
			}
		});
		logger.debug("Submit " + container + " to executor queue.");
		final Future<T> future = executor.submit(container);
		container.addOperationContainerListener(new OperationContainerAdapter<T>() {
			@Override
			public void onTimeout(OperationContainer<T> container, long timeout) {
				future.cancel(true);
			}
		});
		
		return new FutureOperationHandle<T>(future, container);
	}

	@Override
	public List<OperationContainer<?>> getOperations() {
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
