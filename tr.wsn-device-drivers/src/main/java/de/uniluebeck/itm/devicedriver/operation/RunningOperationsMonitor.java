package de.uniluebeck.itm.devicedriver.operation;

import java.util.Set;
import java.util.TreeSet;

import de.uniluebeck.itm.devicedriver.State;

/**
 * Monitor for tracking all operations that are in <code>State.RUNNING</code>.
 * 
 * @author Malte Legenhausen
 */
public class RunningOperationsMonitor {

	/**
	 * Set that store all running operations.
	 */
	private final Set<Operation<?>> operations = new TreeSet<Operation<?>>();
	
	/**
	 * Register a new operation for state observation.
	 * 
	 * @param <T> The return type of the operation.
	 * @param operation The operation that has to be observed.
	 */
	public <T> void monitorState(Operation<T> operation) {
		operation.addListener(new OperationAdapter<T>() {
			@Override
			public void onStateChanged(Operation<T> operation, State oldState, State newState) {
				RunningOperationsMonitor.this.onStateChanged(operation, oldState, newState);
			}
		});
	}
	
	/**
	 * When an operation is in running state it is added to the internal list.
	 * 
	 * @param <T> The return type of the operation.
	 * @param operation The operation that changed the state.
	 * @param oldState The state before.
	 * @param newState The new state.
	 */
	private <T> void onStateChanged(Operation<T> operation, State oldState, State newState) {
		synchronized (operations) {
			if (newState == State.RUNNING) {
				operations.add(operation);
			} else {
				operations.remove(operation);
			}
		}
	}
	
	/**
	 * Returns true if an operation is in <code>State.RUNNING</code> else false.
	 * 
	 * @return true if an operation is in <code>State.RUNNING</code> else false.
	 */
	public boolean isRunning() {
		return !operations.isEmpty();
	}
	
	/**
	 * Returns all running operations.
	 * 
	 * @return A set that contains all running operations.
	 */
	public Set<Operation<?>> getOperations() {
		return operations;
	}
}