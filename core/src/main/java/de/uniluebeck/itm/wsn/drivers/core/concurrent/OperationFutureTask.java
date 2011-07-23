package de.uniluebeck.itm.wsn.drivers.core.concurrent;

import com.google.common.util.concurrent.ListenableFutureTask;

import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;


/**
 * <code>OperationFuture</code> that uses the <code>Future</code> class for handling a threaded operation.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type for of the associated <code>OperationRunnable</code>.
 */
public class OperationFutureTask<T> extends ListenableFutureTask<T> implements OperationFuture<T> {
	
	/**
	 * The <code>OperationRunnable</code> associated with this handle.
	 */
	private final Operation<T> operation;
	
	/**
	 * Constructor.
	 * 
	 * @param operation The <code>OperationRunnable</code> associated with this handle.
	 */
	public OperationFutureTask(final Operation<T> operation) {
		super(operation);
		this.operation = operation;
	}
	
	@Override
	public Operation<T> getOperation() {
		return operation;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		operation.cancel();
		return super.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return operation.isCanceled() || super.isCancelled();
	}
}
