package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * A {@link java.util.concurrent.Future} representing the operations result that is not yet computed.
 *
 * @param <ResultType> the type of the operation result
 */
public interface OperationFuture<ResultType> extends ListenableFuture<ResultType> {

	/**
	 * Returns the associated operation. To register listeners on the operation progress use the operation instance.
	 *
	 * @return the associated operation
	 */
	Operation<ResultType> getOperation();

}
