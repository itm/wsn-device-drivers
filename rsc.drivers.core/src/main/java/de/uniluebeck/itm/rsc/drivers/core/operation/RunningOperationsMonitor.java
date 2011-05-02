package de.uniluebeck.itm.rsc.drivers.core.operation;

import java.util.HashSet;
import java.util.Set;

import de.uniluebeck.itm.rsc.drivers.core.State;
import de.uniluebeck.itm.rsc.drivers.core.event.StateChangedEvent;

/**
 * Monitor for tracking all operations that are in <code>State.RUNNING</code>.
 * 
 * @author Malte Legenhausen
 */
public class RunningOperationsMonitor {

	/**
	 * Set that store all running operations.
	 */
	private final Set<Operation<?>> operations = new HashSet<Operation<?>>();
	
	/**
	 * Register a new operation for state observation.
	 * 
	 * @param <T> The return type of the operation.
	 * @param operation The operation that has to be observed.
	 */
	public <T> void monitorState(final Operation<T> operation) {
		operation.addListener(new OperationListener<T>() {
			@Override
			public void onStateChanged(final StateChangedEvent<T> event) {
				RunningOperationsMonitor.this.onStateChanged(event);
			}
		});
	}
	
	/**
	 * When an operation is in running state it is added to the internal list.
	 * 
	 * @param event The event of an operation that changed the state.
	 */
	private void onStateChanged(final StateChangedEvent<?> event) {
		final Operation<?> operation = (Operation<?>) event.getSource();
		synchronized (operations) {
			if (event.getNewState() == State.RUNNING) {
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
