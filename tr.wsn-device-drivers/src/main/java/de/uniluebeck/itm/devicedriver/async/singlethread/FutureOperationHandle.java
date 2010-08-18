package de.uniluebeck.itm.devicedriver.async.singlethread;

import java.util.concurrent.Future;

import de.uniluebeck.itm.devicedriver.Operation;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;

public class FutureOperationHandle<T> implements OperationHandle<T> {

	private final Future<T> future;
	
	private final Operation<T> operation;
	
	public FutureOperationHandle(Future<T> future, Operation<T> operation) {
		this.future = future;
		this.operation = operation;
	}
	
	@Override
	public void cancel() {
		operation.cancel();
	}

	@Override
	public T get() {
		try {
			return future.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
