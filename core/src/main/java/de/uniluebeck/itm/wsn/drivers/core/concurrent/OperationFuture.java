package de.uniluebeck.itm.wsn.drivers.core.concurrent;

import com.google.common.util.concurrent.ListenableFuture;

import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;

/**
 * Handle that allows the caneling of the operation.
 * 
 * @author Malte Legenhausen
 * 
 * @param <T> The type of the operation result.
 */
public interface OperationFuture<T> extends ListenableFuture<T> {
	
	/**
	 * Returns managed operation behind this future.
	 * 
	 * @return The <code>OperationRunnable</code> that is executed and referenced by this future.
	 */
	Operation<T> getOperation();
}
