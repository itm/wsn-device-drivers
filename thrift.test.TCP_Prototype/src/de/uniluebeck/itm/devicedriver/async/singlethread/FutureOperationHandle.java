package de.uniluebeck.itm.devicedriver.async.singlethread;

import java.util.concurrent.Future;

import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.operation.Operation;


/**
 * <code>OperationHandle</code> that uses the <code>Future</code> class for handling a threaded operation.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> The return type for of the associated <code>Operation</code>.
 */
public class FutureOperationHandle<T> implements OperationHandle<T> {
	
	/**
	 * The <code>Future</code> class that handles the threaded operation.
	 */
	private final Future<T> future;
	
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
	public FutureOperationHandle(Future<T> future, Operation<T> operation) {
		this.future = future;
		this.operation = operation;
	}
	
	@Override
	public State getState() {
		return operation.getState();
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
