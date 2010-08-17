package de.uniluebeck.itm.devicedriver.async;

import de.uniluebeck.itm.devicedriver.Operation;

/**
 * Container that manages a device operation.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The result type of the operation.
 */
public class OperationContainer<T> implements Runnable {

	/**
	 * Enum for all states of an <code>Operation</code>.
	 * 
	 * @author Malte Legenhausen
	 */
	public enum State {
		
		/**
		 * The <code>Operation</code> is waiting in the execution queue.
		 */
		WAITING,
		
		/**
		 * The <code>Operation</code> is currently running.
		 */
		RUNNING,
		
		/**
		 * The <code>Operation</code> has been canceled.
		 */
		CANCELED,
		
		/**
		 * The <code>Operation</code> finsihed with an exception.
		 */
		EXCEPTED,
		
		/**
		 * The <code>Operation</code> is done.
		 */
		DONE
	}
	
	/**
	 * Associated operation with this container.
	 */
	private final Operation<T> operation;
	
	/**
	 * The timeout after which the application will be canceled.
	 */
	private final int timeout;
	
	/**
	 * The callback that is called when the operation has finished, canceled or when an exception occured.
	 */
	private final AsyncCallback<T> callback;
	
	/**
	 * The current state of the <code>Operation</code>.
	 */
	private State state = State.WAITING;
	
	/**
	 * Constructor.
	 * 
	 * @param operation The <code>Operation</code> associated with this container.
	 * @param timeout The timeout after which the operation has to be canceled.
	 * @param callback The callback method for the result.
	 */
	public OperationContainer(Operation<T> operation, int timeout, AsyncCallback<T> callback) {
		this.operation = operation;
		this.timeout = timeout;
		this.callback = callback;
	}
	
	@Override
	public void run() {
		state = State.RUNNING;
		try {
			operation.run(callback);
			if (operation.isCanceled()) {
				state = State.CANCELED;
				callback.onCancel();
			} else {
				state = State.DONE;
				callback.onSuccess(operation.getResult());
			}			
		} catch (RuntimeException e) {
			state = State.EXCEPTED;
			callback.onFailure(e);
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
	public int getTimeout() {
		return timeout;
	}
}
