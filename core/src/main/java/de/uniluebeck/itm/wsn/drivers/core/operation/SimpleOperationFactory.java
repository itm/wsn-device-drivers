package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;


/**
 * Factory for SimpleOperation instances.
 * 
 * @author Malte Legenhausen
 */
@Singleton
public class SimpleOperationFactory implements OperationFactory {

	private final Provider<TimeLimiter> timeLimiterProvider;
	
	@Inject
	public SimpleOperationFactory(Provider<TimeLimiter> timeLimiterProvider) {
		this.timeLimiterProvider = timeLimiterProvider;
	}
	
	@Override
	public <T> Operation<T> create(OperationRunnable<T> runnable, long timeout, OperationCallback<T> callback) {
		return new SimpleOperation<T>(timeLimiterProvider.get(), runnable, timeout, callback);
	}

}
