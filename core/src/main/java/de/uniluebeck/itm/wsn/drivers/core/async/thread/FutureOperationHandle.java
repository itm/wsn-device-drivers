package de.uniluebeck.itm.wsn.drivers.core.async.thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import de.uniluebeck.itm.wsn.drivers.core.State;
import de.uniluebeck.itm.wsn.drivers.core.async.OperationHandle;
import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;


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
	public FutureOperationHandle(final Future<T> future, final Operation<T> operation) {
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
		} catch(final InterruptedException e) {
			throw new RuntimeException(e);
		} catch (final ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

}
