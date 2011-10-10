package de.uniluebeck.itm.wsn.drivers.core.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.util.concurrent.ListenableFutureTask;

import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;


/**
 * <code>OperationFuture</code> that uses the <code>Future</code> class for handling a threaded operation.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type for of the associated <code>OperationRunnable</code>.
 */
public class OperationFutureTask<T> implements OperationFuture<T> {
	
	/**
	 * The <code>OperationRunnable</code> associated with this handle.
	 */
	private final Operation<T> operation;
	
	/**
	 * Reference to the associated task.
	 */
	private final ListenableFutureTask<T> task;
	
	/**
	 * Constructor.
	 * 
	 * @param operation The <code>OperationRunnable</code> associated with this handle.
	 */
	public OperationFutureTask(ListenableFutureTask<T> task, Operation<T> operation) {
		this.task = task;
		this.operation = operation;
	}
	
	@Override
	public Operation<T> getOperation() {
		return operation;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		operation.cancel();
		return task.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return operation.isCanceled() || task.isCancelled();
	}

	@Override
	public void addListener(Runnable listener, Executor exec) {
		task.addListener(listener, exec);
	}

	@Override
	public boolean isDone() {
		return task.isDone();
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		return task.get();
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return task.get(timeout, unit);
	}
}
