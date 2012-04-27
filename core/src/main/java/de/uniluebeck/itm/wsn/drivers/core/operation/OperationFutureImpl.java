package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OperationFutureImpl<V> implements OperationFuture<V> {

	private final SettableFuture future = SettableFuture.create();

	private final Operation<V> operation;

	public OperationFutureImpl(final Operation<V> operation) {
		this.operation = operation;
	}

	@Override
	public Operation<V> getOperation() {
		return operation;
	}

	@Override
	public void addListener(final Runnable listener, final Executor executor) {
		future.addListener(listener, executor);
	}

	@Override
	public boolean cancel(final boolean mayInterruptIfRunning) {
		return future.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return future.isCancelled();
	}

	@Override
	public boolean isDone() {
		return future.isDone();
	}

	@Override
	@SuppressWarnings("unchecked")
	public V get() throws InterruptedException, ExecutionException {
		return (V) future.get();
	}

	@Override
	@SuppressWarnings("unchecked")
	public V get(final long timeout, final TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return (V) future.get(timeout, unit);
	}

	public boolean setException(final Throwable throwable) {
		return future.setException(throwable);
	}

	@SuppressWarnings("unchecked")
	public boolean set(final V result) {
		return future.set(result);
	}
}
