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
	
	private final ProgressManagerFactory progressManagerFactory;
	
	@Inject
	public SimpleOperationFactory(Provider<TimeLimiter> timeLimiterProvider, 
			ProgressManagerFactory progressManagerFactory) {
		this.timeLimiterProvider = timeLimiterProvider;
		this.progressManagerFactory = progressManagerFactory;
	}
	
	@Override
	public <T> Operation<T> create(OperationRunnable<T> runnable, long timeout, OperationCallback<T> callback) {
		ProgressManager progressManager = progressManagerFactory.create(callback);
		return new SimpleOperation<T>(timeLimiterProvider.get(), progressManager, runnable, timeout, callback);
	}

}
