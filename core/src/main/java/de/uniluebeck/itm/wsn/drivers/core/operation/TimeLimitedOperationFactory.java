package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.common.util.concurrent.TimeLimiter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.annotation.Nullable;

import static com.google.common.base.Objects.firstNonNull;


/**
 * Factory for TimeLimitedOperation instances.
 *
 * @author Malte Legenhausen
 */
@Singleton
public class TimeLimitedOperationFactory implements OperationFactory {

	private final Provider<TimeLimiter> timeLimiterProvider;

	private final ProgressManagerFactory progressManagerFactory;

	@Inject
	public TimeLimitedOperationFactory(Provider<TimeLimiter> timeLimiterProvider,
									   ProgressManagerFactory progressManagerFactory) {
		this.timeLimiterProvider = timeLimiterProvider;
		this.progressManagerFactory = progressManagerFactory;
	}

	@Override
	public <T> Operation<T> create(OperationRunnable<T> runnable, long timeout,
								   @Nullable OperationCallback<T> callback) {

		callback = firstNonNull(callback, new OperationCallbackAdapter<T>());
		ProgressManager progressManager = progressManagerFactory.create(callback);
		return new TimeLimitedOperation<T>(timeLimiterProvider.get(), progressManager, runnable, timeout, callback);
	}
}
