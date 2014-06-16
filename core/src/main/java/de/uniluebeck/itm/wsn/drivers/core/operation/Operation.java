package de.uniluebeck.itm.wsn.drivers.core.operation;

import java.util.concurrent.Callable;

/**
 * An operation that is executed on a device.
 *
 * @param <ResultType> the return type of the operation
 *
 * @author Malte Legenhausen
 * @author Daniel Bimschas
 */
public interface Operation<ResultType> extends Callable<ResultType> {

	/**
	 * Request to cancel the operation.
	 */
	void cancel();

	/**
	 * Returns the state of the operation.
	 *
	 * @return The operation state.
	 */
	State getState();

	/**
	 * Returns the timeout in milliseconds for this operation.
	 *
	 * @return The timeout in milliseconds for this operation.
	 */
	long getTimeoutMillis();

	/**
	 * Adds an <code>OperationListener</code> to the stateListener list.
	 *
	 * @param listener
	 * 		The <code>OperationListener</code> that has to be added.
	 */
	void addListener(OperationListener<ResultType> listener);

	/**
	 * Removes the given <code>OperationListener</code> from the stateListener list.
	 *
	 * @param listener
	 * 		The <code>OperationListener</code> that has to be removed.
	 */
	void removeListener(OperationListener<ResultType> listener);
}
