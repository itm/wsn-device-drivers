package de.uniluebeck.itm.devicedriver;


/**
 * A device operation.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type of the operation.
 */
public interface Operation<T> {
	
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
		 * The <code>Operation</code> is done.
		 */
		DONE
	}

	/**
	 * Method that is called when the operation has to be executed.
	 * 
	 * @param monitor The monitor for this operation.
	 */
	void run(Monitor monitor);
	
	/**
	 * Returns the result of the run operation.
	 * 
	 * @return The result of the operation.
	 */
	T getResult();
	
	/**
	 * Cancel the operation.
	 */
	void cancel();
	
	/**
	 * Sets the state of the operation.
	 * 
	 * @param state The <code>OperationState</code>.
	 */
	void setState(State state);
	
	/**
	 * Returns the state of the operation.
	 * 
	 * @return The operation state.
	 */
	State getState();
	
	/**
	 * Returns the timeout for this operation.
	 * 
	 * @return The timeout of the operation.
	 */
	int getTimeout();
	
	/**
	 * Sets the timeout for this operation.
	 * 
	 * @param timeout The timeout of the operation.
	 */
	void setTimeout(int timeout);
}
