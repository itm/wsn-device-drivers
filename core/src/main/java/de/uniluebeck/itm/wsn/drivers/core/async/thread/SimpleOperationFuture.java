package de.uniluebeck.itm.wsn.drivers.core.async.thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.util.concurrent.ListenableFuture;

import de.uniluebeck.itm.wsn.drivers.core.async.OperationFuture;
import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;


/**
 * <code>OperationFuture</code> that uses the <code>Future</code> class for handling a threaded operation.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type for of the associated <code>Operation</code>.
 */
public class SimpleOperationFuture<T> implements OperationFuture<T> {
	
	/**
	 * The <code>Future</code> class that handles the threaded operation.
	 */
	private final ListenableFuture<T> future;
	
	/**
	 * The <code>Operation</code> associated with this handle.
	 */
	private final Operation<T> operation;
	
	/**
	 * Constructor.
	 * 
	 * @param future The <code>Future</code> class that handles the threaded operation.
	 * @param operation The <code>Operation</code> associated with this handle.
	 */
	public SimpleOperationFuture(final ListenableFuture<T> future, final Operation<T> operation) {
		this.future = future;
		this.operation = operation;
	}
	
	@Override
	public Operation<T> getOperation() {
		return operation;
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		operation.cancel();
		return future.cancel(mayInterruptIfRunning);
	}

	@Override
	public void addListener(Runnable listener, Executor exec) {
		future.addListener(listener, exec);
	}

	@Override
	public boolean isCancelled() {
		return operation.isCanceled() || future.isCancelled();
	}

	@Override
	public boolean isDone() {
		return future.isDone();
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		return future.get();
	}

	@Override
	public T get(long timeout, TimeUnit unit) 
			throws InterruptedException, ExecutionException, TimeoutException {
		return future.get(timeout, unit);
	}
}
