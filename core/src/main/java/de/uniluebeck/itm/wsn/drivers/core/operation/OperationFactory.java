package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.inject.ImplementedBy;


/**
 * Factory interface for <code>Operation</code>s.
 * 
 * @author Malte Legenhausen
 */
@ImplementedBy(TimeLimitedOperationFactory.class)
public interface OperationFactory {

	<T> Operation<T> create(OperationRunnable<T> runnable, long timeout, OperationCallback<T> callback);
}
